package io.core9.facets.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertiesQueryBuilder implements QueryBuilder {
	
	private static final String PROPERTIES_KEY = "properties";
	private static final String SEPARATOR = ":";

	@Override
	public Map<String, Object> build(Map<String, List<String>> tree) {
		Map<String, Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> ands = new ArrayList<Map<String,Object>>();
		for(Map.Entry<String, List<String>> entry : tree.entrySet()) {
			ands.add(parseTreeItem(entry.getKey(), entry.getValue()));
		}
		result.put("$and", ands);
		return result;
	}

	private Map<String, Object> parseTreeItem(String namespace, List<String> values) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(values.size() == 1) {
			result.put(PROPERTIES_KEY, namespace + SEPARATOR + values.get(0));
		} else {
			result.put("$or", explode(namespace, values));
		}
		return result;
	}

	private List<Map<String, Object>> explode(String namespace, List<String> values) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(String value : values) {
			Map<String,Object> query = new HashMap<String,Object>();
			query.put(PROPERTIES_KEY, namespace + SEPARATOR + value);
			result.add(query);
		}
		return result;
	}

}