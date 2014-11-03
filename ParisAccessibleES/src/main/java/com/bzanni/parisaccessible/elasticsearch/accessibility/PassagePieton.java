package com.bzanni.parisaccessible.elasticsearch.accessibility;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class PassagePieton implements JestBusiness {

	@JestId
	private String id;
	
	private GeoPoint start;

	private GeoPoint end;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeoPoint getStart() {
		return start;
	}

	public void setStart(GeoPoint start) {
		this.start = start;
	}

	public GeoPoint getEnd() {
		return end;
	}

	public void setEnd(GeoPoint end) {
		this.end = end;
	}

}
