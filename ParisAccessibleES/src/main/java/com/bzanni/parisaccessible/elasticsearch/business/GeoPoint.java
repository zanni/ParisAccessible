package com.bzanni.parisaccessible.elasticsearch.business;

import java.util.ArrayList;

public class GeoPoint extends ArrayList<Double> {
	// private double lat;
	// private double lon;

	public GeoPoint(double latitude, double longitude) {
		super();
		this.add(longitude);
		this.add(latitude);
//		this.lat = latitude;
//		this.lon = longitude;
	}

	public Double getLat() {
		return this.get(1);
	}

	public Double getLon() {
		return this.get(0);
	}
}
