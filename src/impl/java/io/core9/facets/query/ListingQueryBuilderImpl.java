package io.core9.facets.query;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class ListingQueryBuilderImpl implements ListingQueryBuilder {

	@Override
	public ProductQueryBuilder start() {
		return new ProductQueryBuilderImpl();
	}

	
}
