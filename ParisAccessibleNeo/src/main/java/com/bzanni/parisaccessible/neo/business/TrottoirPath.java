package com.bzanni.parisaccessible.neo.business;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = "TROTTOIR")
public class TrottoirPath {
	@StartNode
	Location start;
	@EndNode
	Location end;

	Double cost;
	
	public TrottoirPath(Location start, Location end, Double cost) {
		this.start = start;
		this.end = end;
		this.cost = cost;
	}

}
