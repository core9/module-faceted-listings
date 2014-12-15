package io.core9.facets.query;

import com.mongodb.DBObject;

public class ProductQueryBuilderImpl implements ProductQueryBuilder {
	private ProductQueryTree tree = new ProductQueryTree();
	 
	@Override
	public ProductQueryBuilderImpl add(String queryString) {
		parseQueryString(queryString);
		return this;
	}
	
	private void parseQueryString(String queryString) {
		int separator = queryString.indexOf(':');
		String type = queryString.substring(0, separator);
		int secondSeparator = queryString.indexOf(':', separator + 1);
		String namespace = queryString.substring(separator +1, secondSeparator);
		String value = queryString.substring(secondSeparator + 1);
		tree.add(type, namespace, value);
	}
	
	@Override
	public DBObject build() {
		return tree.build();
	}

}
