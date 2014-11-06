package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//@NodeEntity
public class Location {

	// @GraphId
	private String id;

	// @RelatedTo(type = "PIETON", direction = Direction.BOTH)
	private Set<Location> pietons;

	// @RelatedTo(type = "TROTTOIR", direction = Direction.BOTH)
	private Set<Location> trottoirs;

	// @RelatedTo(type = "TRANSPORT", direction = Direction.OUTGOING)
	private Set<Location> transports;

	public Location(String id) {
		this.id = id;
	}

	public PassagePietonPath mapPassagePieton(Location to, Double cost) {
		return new PassagePietonPath(this, to, cost);
	}

	public TransportPath mapTransport(Location to, Double cost) {
		return new TransportPath(this, to, cost);
	}

	public TrottoirPath mapTrottoir(Location to, Double cost) {
		return new TrottoirPath(this, to, cost);
	}

	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("id", this.id);
		return res;
	}

}
