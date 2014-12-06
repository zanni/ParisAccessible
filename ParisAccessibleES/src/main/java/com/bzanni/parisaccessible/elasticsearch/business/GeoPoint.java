package com.bzanni.parisaccessible.elasticsearch.business;

import java.util.ArrayList;

public class GeoPoint  {
	/**
	 * 
	 */

	private ArrayList<Double> pin;
	// private double lat;
	// private double lon;

	public GeoPoint(double latitude, double longitude) {
		pin = new ArrayList<Double>();
		
		pin.add(longitude);
		pin.add(latitude);
		
//		this.lat = latitude;
//		this.lon = longitude;
	}

	public Double getLat() {
		return pin.get(0);
	}

	public Double getLon() {
		return pin.get(1);
	}
}
