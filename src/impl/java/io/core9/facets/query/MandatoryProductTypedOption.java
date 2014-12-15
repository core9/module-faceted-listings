package io.core9.facets.query;

import java.util.ArrayList;
import java.util.List;

public class MandatoryProductTypedOption extends ProductTypedOption {
	
	private List<String> namespacesUsed = new ArrayList<String>();
	
	@Override
	public ProductTypedOption add(String namespace, String value) {
		if(!namespacesUsed.contains(namespace)) {
			if(tree.get(namespace) != null) {
				tree.remove(namespace);
			}
			namespacesUsed.add(namespace);
		}
		super.add(namespace, value);
		return this;
	}

}
