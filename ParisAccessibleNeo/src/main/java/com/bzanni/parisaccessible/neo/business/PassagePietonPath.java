package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = "PIETON")
public class PassagePietonPath {

	@StartNode
	Location start;
	@EndNode
	Location end;

	Double cost;

	public PassagePietonPath(Location start, Location end, Double cost) {
		this.start = start;
		this.end = end;
		this.cost = cost;
	}
	
	public Map<String, Object> getMap(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cost", this.cost);
		return res;
	}

}
