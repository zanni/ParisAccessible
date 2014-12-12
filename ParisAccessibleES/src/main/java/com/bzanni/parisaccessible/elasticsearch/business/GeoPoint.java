package com.bzanni.parisaccessible.elasticsearch.business;

import java.util.ArrayList;

public class GeoPoint extends ArrayList<Double>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8233066789180416479L;

	/**
	 * 
	 */

	public GeoPoint(){
		super();
		this.add(0D);
		this.add(0D);
	}
	
	public GeoPoint(double latitude, double longitude) {
		
		this.add(longitude);
		this.add(latitude);
		
	}

	public Double getLat() {
		return this.get(1);
	}

	public Double getLon() {
		return this.get(0);
	}
	
	public void setLat(Double value){
		this.set(1, value);
	}
	
	public void setLon(Double value){
		this.set(0, value);
	}
}
