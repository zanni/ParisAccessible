package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class GtfsAgency implements JestBusiness {

	@JestId
	private String id;
	private String name;
	private String url;
	private String timezone;
	private String lang;
	private String phone;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
