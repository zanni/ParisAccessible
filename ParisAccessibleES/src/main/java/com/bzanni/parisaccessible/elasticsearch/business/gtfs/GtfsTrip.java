package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

import io.searchbox.annotations.JestId;

public class GtfsTrip implements JestBusiness{
	@JestId
	private String id;
	
	private String route_id;
	
	private String service_id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoute_id() {
		return route_id;
	}

	public void setRoute_id(String route_id) {
		this.route_id = route_id;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

}
