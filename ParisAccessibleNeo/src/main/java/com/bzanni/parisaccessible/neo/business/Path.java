package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

public class Path {

	private Location start;
	private Location end;
	private Double cost;
	private Double distance;
	private boolean accessible = true;
	private String type;

	public Path() {
	}

	public Path(Location start, Location end, Double distance,
			Double cost) {
		this.start = start;
		this.end = end;
		this.cost = cost;
		this.distance = distance;
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

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", this.cost);
		return res;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getType() {
		return "PATH";
	}


}
