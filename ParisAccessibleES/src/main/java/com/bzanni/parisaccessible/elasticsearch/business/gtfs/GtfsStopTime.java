package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

import io.searchbox.annotations.JestId;

public class GtfsStopTime implements JestBusiness{
	
	@JestId
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
