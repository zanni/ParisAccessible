package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import io.searchbox.annotations.JestId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class GtfsService implements JestBusiness {

	@JestId
	private String id;

	private Date startDate;

	private Date endDate;

	private List<Date> calendar;

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

	public List<Date> getCalendar() {
		return calendar;
	}

	public void setCalendar(List<Date> calendar) {
		this.calendar = calendar;
	}

	public void addCalendar(Date date) {
		if (this.calendar == null) {
			this.calendar = new ArrayList<Date>();
		}
		this.calendar.add(date);
	}

}
