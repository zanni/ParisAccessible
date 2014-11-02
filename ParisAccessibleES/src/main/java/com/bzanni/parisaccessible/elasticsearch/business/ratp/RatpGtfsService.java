package com.bzanni.parisaccessible.elasticsearch.business.ratp;

import io.searchbox.annotations.JestId;

import java.util.Date;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class RatpGtfsService implements JestBusiness{

	@JestId
	private String id;

	private Date startDate;

	private Date endDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


}
