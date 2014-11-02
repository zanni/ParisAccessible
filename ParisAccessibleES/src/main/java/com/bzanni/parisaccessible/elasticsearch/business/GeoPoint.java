package com.bzanni.parisaccessible.elasticsearch.business;

public class GeoPoint {
	private double lat;
	private double lon;

	public GeoPoint(double latitude, double longitude) {
		this.lat = latitude;
		this.lon = longitude;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
}
