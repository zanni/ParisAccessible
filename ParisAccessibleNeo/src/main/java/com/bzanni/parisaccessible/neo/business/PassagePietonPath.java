package com.bzanni.parisaccessible.neo.business;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "PIETON")
public class PassagePietonPath extends Path {
	
	

	public PassagePietonPath(Location start, Location end, Double distance,
			Double cost) {
		super(start, end, distance, cost);
	}
}
