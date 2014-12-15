package io.core9.facets.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MandatoryProductTypedOption extends ProductTypedOption {
	
	private ProductTypedOption original;
	
	@Override
	public MandatoryProductTypedOption add(String namespace, String value) {
		original.add(namespace, value);
		return this;
	}
	
	public MandatoryProductTypedOption addMandatory(String namespace, String value) {
		super.add(namespace, value);
		return this;
	}
	
	public MandatoryProductTypedOption parseFrom(ProductTypedOption original) {
		this.original = original;
		return this;
	}
	
	@Override
	public Map<String,Object> build(QueryBuilder builder) {
		List<Map<String,Object>> ands = new ArrayList<Map<String,Object>>();
		ands.add(original.build(builder));
		ands.add(super.build(builder));
		Map<String,Object> result = new HashMap<String,Object>(1);
		result.put("$and", ands);
		return result;
	}

}
