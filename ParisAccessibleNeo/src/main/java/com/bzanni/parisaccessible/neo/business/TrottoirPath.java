package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

//@RelationshipEntity(type = "TROTTOIR")
public class TrottoirPath {
	// @StartNode
	private Location start;
	// @EndNode
	private Location end;

	private Double cost;

	public TrottoirPath(Location start, Location end, Double cost) {
		this.setStart(start);
		this.setEnd(end);
		this.setCost(cost);
	}

	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", this.getCost());
		return res;
	}

	public Location getStart() {
		return start;
	}

	public void setStart(Location start) {
		this.start = start;
	}

	public Location getEnd() {
		return end;
	}

	public void setEnd(Location end) {
		this.end = end;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

}
