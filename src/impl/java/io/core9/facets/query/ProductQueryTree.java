package io.core9.facets.query;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ProductQueryTree {
	
	private Map<Type, ProductTypedOption> tree = new HashMap<Type, ProductTypedOption>();
	
	public void add(String type, String namespace, String value) {
		Type entryType = Type.valueOf(type.toUpperCase());
		if(tree.containsKey(entryType)) {
			tree.get(entryType).add(namespace, value);
		} else {
			tree.put(entryType, new ProductTypedOption(namespace, value));
		}
	}

	public enum Type {
		PROPERTIES, TEXT, FIELD
	}

	public DBObject build() {
		BasicDBObject query = new BasicDBObject();
		for(Map.Entry<Type, ProductTypedOption> entry : tree.entrySet()) {
			switch(entry.getKey()) {
			case PROPERTIES:
				query.putAll(entry.getValue().build(new PropertiesQueryBuilder()));
				break;
			case TEXT:
				query.putAll(entry.getValue().build(new TextQueryBuilder()));
				break;
			case FIELD:
				query.putAll(entry.getValue().build(new FieldQueryBuilder()));
				break;
			}
		}
		return query;
	}
}
