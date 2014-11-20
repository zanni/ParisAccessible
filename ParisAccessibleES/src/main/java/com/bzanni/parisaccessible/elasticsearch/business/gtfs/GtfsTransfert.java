package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

public class GtfsTransfert {
	private String toStopId;
	private String type;
	private Double time;

	public GtfsTransfert(String toStopId, String type, Double time) {
		this.type = type;
		this.toStopId = toStopId;
		this.time = time;
	}

	public String getToStopId() {
		return toStopId;
	}

	public void setToStopId(String toStopId) {
		this.toStopId = toStopId;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
