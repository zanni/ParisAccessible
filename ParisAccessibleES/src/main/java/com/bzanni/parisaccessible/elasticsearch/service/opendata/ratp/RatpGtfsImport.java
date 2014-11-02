package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.service.util.ParisAccessibleConfigurationBean;

@Service
public class RatpGtfsImport {

	@Resource
	private RatpGtfsRouteCsvImport ratpGtfsRouteCsvImport;

	@Resource
	private RatpGtfsRouteAccessibilityCsvImport ratpGtfsRouteAccessibilityCsvImport;

	@Resource
	private RatpGtfsStopCsvImport ratpGtfsStopCsvImport;

	@Resource
	private RatpGtfsStopAccessibilityCsvImport ratpGtfsStopAccessibilityCsvImport;

	@Resource
	private RatpGtfsServiceCsvImport ratpGtfsServiceCsvImport;

	@Resource
	private RatpGtfsTripCsvImport ratpGtfsTripCsvImport;

	@Resource
	private ParisAccessibleConfigurationBean configuration;

	public void importTrip(String path, int bulk) {
		ratpGtfsTripCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

	public void importService(String path, int bulk) {
		ratpGtfsServiceCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

	public void importRoute(String path, int bulk) {
		ratpGtfsRouteCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

	public void importStop(String path, int bulk) {
		ratpGtfsStopCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

	public void importStopAccessibility(String path, int bulk) {
		ratpGtfsStopAccessibilityCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

	public void importRouteAccessibility(String path, int bulk) {
		ratpGtfsRouteAccessibilityCsvImport.importData(configuration.getInjectPath()+path, bulk);
	}

}
