package com.bzanni.parisaccessible.elasticsearch.business.ratp;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

import io.searchbox.annotations.JestId;

public class RatpStopTime implements JestBusiness{
	
	@JestId
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
