package com.bzanni.parisaccessible.elasticsearch.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParisAccessibleConfigurationBean {

	@Value("${ratp_gtfs_index_name}")
	private String ratpGtfsIndexName;

	@Value("${inject_path}")
	private String injectPath;

	public String getRatpGtfsIndexName() {
		return ratpGtfsIndexName;
	}

	public void setRatpGtfsIndexName(String ratpGtfsIndexName) {
		this.ratpGtfsIndexName = ratpGtfsIndexName;
	}
	

	public String getInjectPath() {
		return injectPath;
	}

	public void setInjectPath(String injectPath) {
		this.injectPath = injectPath;
	}
}
