package io.core9.facets.query;

import java.util.Map;

public class ProductQueryBuilderImpl implements ProductQueryBuilder {
	private ProductQueryTree tree = new ProductQueryTree();
	 
	@Override
	public ProductQueryBuilderImpl add(String queryString) {
		parseQueryString(queryString, false);
		return this;
	}
	
	@Override
	public ProductQueryBuilder addAndClause(String queryString) {
		parseQueryString(queryString, true);
		return this;
	}
	
	private void parseQueryString(String queryString, boolean mandatory) {
		int separator = queryString.indexOf(':');
		String type = queryString.substring(0, separator);
		int secondSeparator = queryString.indexOf(':', separator + 1);
		String namespace = queryString.substring(separator +1, secondSeparator);
		String value = queryString.substring(secondSeparator + 1);
		tree.add(type, namespace, value, mandatory);
	}
		
	@Override
	public Map<String, Object> build() {
		return tree.build();
	}

}
