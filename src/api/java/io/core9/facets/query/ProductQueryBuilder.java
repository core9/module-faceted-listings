package io.core9.facets.query;

import java.util.Map;

public interface ProductQueryBuilder {

	Map<String,Object> build();
	
	ProductQueryBuilder add(String queryString);

	ProductQueryBuilder addAndClause(String string);
	
}
