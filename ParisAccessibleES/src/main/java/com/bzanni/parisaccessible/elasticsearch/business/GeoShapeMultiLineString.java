package com.bzanni.parisaccessible.elasticsearch.business;

import java.util.List;

public class GeoShapeMultiLineString extends GeoShape{


	private List<List<List<Double>>> coordinates;

	public List<List<List<Double>>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<List<Double>>> coordinates) {
		this.coordinates = coordinates;
	}
	
}
