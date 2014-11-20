package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Path {

	private Location start;
	private Location end;
	private boolean accessible = true;
	private String type;

	public Path() {
	}

	public Path(Location start, Location end) {
		this.start = start;
		this.end = end;
//		this.cost = cost;
//		this.distance = distance;
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


	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	@JsonIgnore
	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", CostCompute.computeDistance(
				this.getStart(), this.getEnd()));
		return res;
	}

	public String getType() {
		return "PATH";
	}


}
