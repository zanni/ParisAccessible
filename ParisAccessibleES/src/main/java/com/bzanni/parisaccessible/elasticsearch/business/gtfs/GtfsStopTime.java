package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

import io.searchbox.annotations.JestId;

public class GtfsStopTime implements JestBusiness{
	
	@JestId
	private String id;
	
	private String time;
	
	private String trip_id;
	
	private String stop_id;
	
	private String seq;
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getStop_id() {
		return stop_id;
	}

	public void setStop_id(String stop_id) {
		this.stop_id = stop_id;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
