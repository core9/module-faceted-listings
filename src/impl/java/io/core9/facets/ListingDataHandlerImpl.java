package io.core9.facets;

import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.datahandler.factories.ContentDataHandler;
import io.core9.plugin.widgets.datahandler.factories.ContentDataHandlerConfig;
import io.core9.plugin.widgets.datahandler.factories.CustomGlobal;
import io.core9.plugin.widgets.datahandler.factories.CustomVariable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@PluginImplementation
public class ListingDataHandlerImpl implements ListingDataHandler<ContentDataHandlerConfig> {
	
	private static final String NAME = "FacetedListing";
	private static final String CTX_DATABASE = "database";
	private static final String CTX_PREFIX   = "prefix";
	
	@InjectPlugin
	private MongoDatabase database;
		
	@InjectPlugin
	private ContentDataHandler<ContentDataHandlerConfig> contentHandlerFactory;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return ContentDataHandlerConfig.class;
	}

	@Override
	public DataHandler<ContentDataHandlerConfig> createDataHandler(final DataHandlerFactoryConfig options) {
		final ContentDataHandlerConfig config = (ContentDataHandlerConfig) options;
		
		return new DataHandler<ContentDataHandlerConfig>(){

			@SuppressWarnings("unchecked")
			@Override
			public Map<String, Object> handle(Request req) {
				Map<String,Object> result = new HashMap<String, Object>(1);
				Map<String,Deque<String>> params = req.getQueryParams();
				int page = 1;
				if(params.size() > 0) {
					if(params.containsKey("clear")) {
						redirectToClearedParameters(req, params);
					} else if(params.containsKey("page")) {
						page = Integer.parseInt(params.remove("page").getFirst());
					}
				}
				
				DBObject listing = getListingPageObject(req.getVirtualHost(), req);
				List<String> andProperties = new ArrayList<String>();
				Map<String, Deque<String>> orProperties = new HashMap<String, Deque<String>>();
				for(Map.Entry<String, Deque<String>> param : params.entrySet()) {
					if(param.getValue().size() > 1) {
						orProperties.put(param.getKey(), param.getValue());
					} else {
						andProperties.add(param.getKey().replaceFirst("properties:", "") + ":" + param.getValue().getFirst());
					}
				}
				int perPage = config.getPager().getResultsPerPage();
				int start = ((page - 1) * perPage) + 1;
				int end  = page  * perPage;
				int index = 0;
				boolean moreAvailable = false;
				List<DBObject> resultProducts = new ArrayList<DBObject>();
				for(DBObject product : (List<DBObject>) listing.removeField("products")) {
					List<String> productProperties = (List<String>) product.get("properties");
					if(productProperties != null && productProperties.containsAll(andProperties) && containsOrClause(productProperties, orProperties) && ++index >= start) {
						resultProducts.add(product);
						if(index == end) {
							moreAvailable = true;
							break;
						}
					}
				}				
				result.put("products", resultProducts);
				result.put("content", listing);
				result.put("facets", getFacet(listing, params));
				result.put("more", moreAvailable);
				result.put("page", page);
				putCustomVariablesOnContext(req, listing);
				return result;
			}
			
			private boolean containsOrClause(List<String> properties, Map<String, Deque<String>> ors) {
				for(Map.Entry<String, Deque<String>> or : ors.entrySet()) {
					if(!containsOrClause(properties, or.getKey().replace("properties:", ""), or.getValue())) {
						return false;
					}
				}
				return true;
			}
			
			private boolean containsOrClause(List<String> properties, String type, Deque<String> values) {
				for(String value : values) {
					if(properties.contains(type + ":" + value)) {
						return true;
					}
				}
				return false;
			}

			/**
			 * TODO: WTF?!
			 * @param listing
			 * @param params
			 * @return
			 */
			private DBObject getFacet(DBObject listing, Map<String, Deque<String>> params) {
				DBObject facets = (DBObject) listing.removeField("facets");
				for(String currentlySelectedFacet : params.keySet()) {
					DBObject facet = (DBObject) facets.get(currentlySelectedFacet);
					if(facet != null) {
						@SuppressWarnings("unchecked")
						List<DBObject> facetOptions = (List<DBObject>) facet.get("options");
						if(facetOptions != null) {
							for(String optionId : params.get(currentlySelectedFacet)) {
								for(DBObject option : facetOptions) {
									if(option.get("id").equals(optionId)) {
										option.put("used", true);
									}
								}
							}
						}
					}
				}
				return facets;
			}

			protected DBObject getListingPageObject(VirtualHost vhost, Request req) {
				DBCollection coll = database.getCollection(vhost.getContext(CTX_DATABASE), vhost.getContext(CTX_PREFIX) + config.getContentType());
				Map<String,Object> query = CustomGlobal.convertToQuery(config.getFields(), req, options.getComponentName());
				return coll.findOne(new BasicDBObject(query));
			}

			private void putCustomVariablesOnContext(Request req, DBObject content) {
				if(config.getCustomVariables() != null) {
					for(CustomVariable var : config.getCustomVariables()) {
						if(var.isManual()) {
							req.getResponse().addGlobal(var.getKey(), var.getValue());
						} else {
							req.getResponse().addGlobal(var.getKey(), content.get(var.getValue()));
						}
					}
				}
			}

			@Override
			public ContentDataHandlerConfig getOptions() {
				return (ContentDataHandlerConfig) options;
			}
		};
	}
	
	protected void redirectToClearedParameters(Request req, Map<String, Deque<String>> params) {
		Deque<String> clear = params.remove("clear");
		for(String clearItem : clear) {
			int separator = clearItem.lastIndexOf(":");
			String group = clearItem.substring(0, separator);
			String value = clearItem.substring(separator + 1);
			params.get(group).remove(value);
		}
		parseRedirectionString(params);
		req.getResponse().sendRedirect(301, req.getPath() + "?" + parseRedirectionString(params));
	}

	private String parseRedirectionString(Map<String, Deque<String>> params) {
		String result = "";
		for(Map.Entry<String, Deque<String>> param : params.entrySet()) {
			if(!param.getValue().isEmpty()) {
				for(String value : param.getValue()) {
					result += param.getKey() + "=" + value + "&";
				}
				result.substring(0, result.length() - 1);
			}
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

}
