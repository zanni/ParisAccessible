package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "PIETON")
public class PassagePietonPath extends Path implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2672361275594713488L;

	public PassagePietonPath(Location start, Location end, Double distance,
			Double cost) {
		super(start, end, distance, cost);
	}

	@Override
	public String getType() {
		return "PIETON";
	}
}
