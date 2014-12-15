package io.core9.facets.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProductTypedOption {
	
	private Map<String, List<String>> tree = new TreeMap<String, List<String>>();

	public ProductTypedOption(String namespace, String value) {
		this.add(namespace, value);
	}

	public Map<String,Object> build(QueryBuilder builder) {
		return builder.build(tree);
	}

	public void add(String namespace, String value) {
		List<String> items = tree.get(namespace);
		if(items == null) {
			items = new ArrayList<String>();
			tree.put(namespace, items);
		}
		items.add(value);
	}

}
