package io.core9.facets.query;

import java.util.List;
import java.util.Map;

public interface QueryBuilder {

	Map<String,Object> build(Map<String, List<String>> tree);

}
