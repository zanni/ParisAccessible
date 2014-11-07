package com.bzanni.parisaccessible.neo.business;

import java.util.HashMap;
import java.util.Map;

public class Location {

	private Long graphId;

	private String id;

	private Double lat;

	private Double lon;

	public Location(String id, Double lat, Double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}

	public PassagePietonPath mapPassagePieton(Location to, Double speed) {
		return new PassagePietonPath(this, to, CostCompute.computeDistance(
				this, to), CostCompute.computeCost(this, to, speed));
	}

	public TransportPath mapTransport(Location to, Double speed) {
		return new TransportPath(this, to, CostCompute.computeDistance(
				this, to), CostCompute.computeCost(this, to, speed));
	}

	public TrottoirPath mapTrottoir(Location to, Double speed) {
		return new TrottoirPath(this, to, CostCompute.computeDistance(
				this, to), CostCompute.computeCost(this, to, speed));
	}

	public Map<String, Object> getMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("id", this.id);
		res.put("lat", this.lat);
		res.put("lon", this.lon);
		return res;
	}

	public Long getGraphId() {
		return graphId;
	}

	public void setGraphId(Long graphId) {
		this.graphId = graphId;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

}
