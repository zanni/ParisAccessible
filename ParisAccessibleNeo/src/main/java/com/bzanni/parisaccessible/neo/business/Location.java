package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8413877462450872080L;

	@JsonIgnore
	private Long graphId;

	private String id;

	private Double lat;

	private Double lon;

	private String label;

	public Location() {
	}

	public Location(String label, String id, Double lat, Double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.label = label;
	}

	public PassagePietonPath mapPassagePieton(Location to, Double speed) {
		return new PassagePietonPath(this, to);
	}

	public TransportPath mapTransport(Location to, Double speed) {
		return new TransportPath(this, to);
	}

	public TrottoirPath mapTrottoir(Location to, Double speed) {
		return new TrottoirPath(this, to);
	}

	@JsonIgnore
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

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
