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
	}
	
	public GeoPoint(double latitude, double longitude) {
		
		this.add(longitude);
		this.add(latitude);
		
	}

	public Double getLat() {
		if(this.size() == 2){
			return this.get(1);
		}
		return null;
	}

	public Double getLon() {
		if(this.size() == 2){
			return this.get(0);
		}
		return null;
	}
	
	public void setLat(Double value){
		this.add(0, value);
	}
	
	public void setLon(Double value){
		this.add(1, value);
	}
}
