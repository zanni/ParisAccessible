package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class GtfsTripCsvImportThread extends Thread {
	@Resource
	private GtfsImport ratpGtfsImport;
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
