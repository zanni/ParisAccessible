package com.bzanni.parisaccessible.elasticsearch.business;

import java.util.List;

public class GeoShapeLineString extends GeoShape{

	private List<List<Double>> coordinates;

	
	public List<List<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double>> coordinates) {
		this.coordinates = coordinates;
	}


}
