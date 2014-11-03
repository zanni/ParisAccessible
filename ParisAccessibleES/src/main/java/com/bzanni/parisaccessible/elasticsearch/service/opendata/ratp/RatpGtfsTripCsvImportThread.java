package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class RatpGtfsTripCsvImportThread extends Thread {
	@Resource
	private RatpGtfsImport ratpGtfsImport;
	private String csvFile;
	private int bulk;
	
	
	@Override
	public void run() {
 
		ratpGtfsImport.importTrip(getCsvFile(), bulk);
	}


	public String getCsvFile() {
		return csvFile;
	}


	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}


	public int getBulk() {
		return bulk;
	}


	public void setBulk(int bulk) {
		this.bulk = bulk;
	}
}
