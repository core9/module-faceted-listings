package io.core9.facets.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class ProductTypedOption {

	protected Map<String, List<String>> tree = new TreeMap<String, List<String>>();

	public Map<String,Object> build(QueryBuilder builder) {
		return builder.build(tree);
	}

	public ProductTypedOption add(String namespace, String value) {
		List<String> items = tree.get(namespace);
		if(items == null) {
			items = new ArrayList<String>();
			tree.put(namespace, items);
		}
		items.add(value);
		return this;
	}
	
	public ProductTypedOption parseFrom(ProductTypedOption original) {
		this.tree = original.tree;
		return this;
	}
}
