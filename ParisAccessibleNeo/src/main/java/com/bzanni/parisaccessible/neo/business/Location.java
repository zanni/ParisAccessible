package com.bzanni.parisaccessible.neo.business;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Location {

	@GraphId
	private Long id;
	@RelatedTo(type = "PIETON", direction = Direction.BOTH)
	private Set<Location> pietons;

	@RelatedTo(type = "TROTTOIR", direction = Direction.BOTH)
	private Set<Location> trottoirs;

	@RelatedTo(type = "TRANSPORT", direction = Direction.OUTGOING)
	private Set<Location> transports;

	public PassagePietonPath mapPassagePieton(Location to, Double cost) {
		return new PassagePietonPath(this, to, cost);
	}
	
	public TransportPath mapTransport(Location to, Double cost) {
		return new TransportPath(this, to, cost);
	}

	public TrottoirPath mapTrottoir(Location to, Double cost) {
		return new TrottoirPath(this, to, cost);
	}

}
