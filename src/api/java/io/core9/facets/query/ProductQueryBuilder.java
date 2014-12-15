package io.core9.facets.query;

import com.mongodb.DBObject;

public interface ProductQueryBuilder {

	DBObject build();
	
	ProductQueryBuilder add(String queryString);
	
}
