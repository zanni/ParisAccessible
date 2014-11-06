package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

//@RelationshipEntity(type = "TROTTOIR")
public class TrottoirPath {
	// @StartNode
	Location start;
	// @EndNode
	Location end;

	Double cost;

	public TrottoirPath(Location start, Location end, Double cost) {
		this.start = start;
		this.end = end;
		this.cost = cost;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", this.cost);
		return res;
	}

}
