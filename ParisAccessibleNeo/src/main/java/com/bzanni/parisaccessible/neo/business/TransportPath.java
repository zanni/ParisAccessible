package com.bzanni.parisaccessible.neo.business;

import org.neo4j.graphdb.DynamicRelationshipType;

public class TransportPath extends Path {

	public TransportPath(Location start, Location end, Double distance,
			Double cost) {
		super(start, end, distance, cost);
	}
	
	@Override
	public DynamicRelationshipType getRelationshipType() {
		return DynamicRelationshipType.withName("TRANSPORT");
	}
}
