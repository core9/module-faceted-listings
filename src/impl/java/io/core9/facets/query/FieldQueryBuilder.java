package io.core9.facets.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldQueryBuilder implements QueryBuilder {

	@Override
	public Map<String, Object> build(Map<String, List<String>> tree) {
		Map<String, Object> result = new HashMap<String,Object>();
		for(Map.Entry<String, List<String>> entry : tree.entrySet()) {
			result.put(entry.getKey(), parseValue(entry.getValue()));
		}
		return result;
	}

	private Object parseValue(List<String> values) {
		if(values.size() > 1) {
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("$in", parseValueType(values));
			return result;
		} else {
			return parseValueType(values.get(0));
		}
	}

	private List<?> parseValueType(List<String> values) {
		String first = values.get(0);
		int separator = first.indexOf(':');
		switch(first.substring(0, separator)) {
		case "bool":
			return parseBoolean(values, separator + 1);
		case "int":
			return parseInt(values, separator + 1);
		case "str":
		default:
			return parseString(values, separator  + 1);
		}
	}

	private Object parseValueType(String value) {
		int separator = value.indexOf(':');
		switch(value.substring(0, separator)) {
		case "bool":
			return parseBoolean(value, separator + 1);
		case "int":
			return parseInt(value, separator + 1);
		case "str":
		default:
			return parseString(value, separator  + 1);
		}		
	}
	
	private Boolean parseBoolean(String value, int from) {
		return Boolean.parseBoolean(value.substring(from));
	}

	private String parseString(String value, int from) {
		return value.substring(from);
	}

	private Integer parseInt(String value, int from) {
		return Integer.parseInt(value.substring(from));
	}
	

	private List<Boolean> parseBoolean(List<String> values, int from) {
		List<Boolean> result = new ArrayList<Boolean>(values.size());
		for(String value : values) {
			result.add(Boolean.parseBoolean(value.substring(from)));
		}
		return result;
	}

	private List<Integer> parseInt(List<String> values, int from) {
		List<Integer> result = new ArrayList<Integer>(values.size());
		for(String value : values) {
			result.add(Integer.parseInt(value.substring(from)));
		}
		return result;
	}

	private List<String> parseString(List<String> values, int from) {
		List<String> result = new ArrayList<String>(values.size());
		for(String value : values) {
			result.add(value.substring(from));
		}
		return result;
	}

}
