package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = "PIETON")
public class PassagePietonPath {

	@StartNode
	private Location start;
	@EndNode
	private Location end;

	private Double cost;

	public PassagePietonPath(Location start, Location end, Double cost) {
		this.setStart(start);
		this.setEnd(end);
		this.cost = cost;
	}
	
	public Map<String, Object> getMap(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", this.cost);
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

}
