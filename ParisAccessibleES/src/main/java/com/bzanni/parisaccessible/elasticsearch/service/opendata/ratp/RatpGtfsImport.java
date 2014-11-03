package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp.accessibility.RatpGtfsRouteAccessibilityCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp.accessibility.RatpGtfsStopAccessibilityCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.util.ParisAccessibleConfigurationBean;

@Service
public class RatpGtfsImport {

	@Resource
	private RatpGtfsAgencyCsvImport ratpGtfsAgencyCsvImport;

	@Resource
	private RatpGtfsRouteCsvImport ratpGtfsRouteCsvImport;

	@Resource
	private RatpGtfsRouteAccessibilityCsvImport ratpGtfsRouteAccessibilityCsvImport;

	@Resource
	private RatpGtfsStopCsvImport ratpGtfsStopCsvImport;

	@Resource
	private RatpGtfsStopTransfertCsvImport ratpGtfsStopTransfertCsvImport;

	@Resource
	private RatpGtfsStopTimeCsvImport ratpGtfsStopTimeCsvImport;

	@Resource
	private RatpGtfsStopAccessibilityCsvImport ratpGtfsStopAccessibilityCsvImport;

	@Resource
	private RatpGtfsServiceCsvImport ratpGtfsServiceCsvImport;

	@Resource
	private RatpGtfsServiceCalendarCsvImport ratpGtfsServiceCalendarCsvImport;

	@Resource
	private RatpGtfsTripCsvImport ratpGtfsTripCsvImport;

	@Resource
	private ParisAccessibleConfigurationBean configuration;

	private String getGtfs(String fileName) {
		return configuration.getGtfsPath() +"/" + fileName;
	}

	private String getAccessibility(String fileName) {
		return configuration.getAccessibilityPath() +"/"  + fileName;
	}

	public void importAgency(int bulk) {
		ratpGtfsAgencyCsvImport.importData(
				getGtfs(configuration.getGtfsAgencyFilename()), bulk);
	}

	public void importAgency(String fileName, int bulk) {
		ratpGtfsAgencyCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importTrip(int bulk) {
		ratpGtfsTripCsvImport.importData(
				getGtfs(configuration.getGtfsTripFilename()), bulk);
	}

	public void importTrip(String fileName, int bulk) {
		ratpGtfsTripCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importService(int bulk) {
		ratpGtfsServiceCsvImport.importData(
				getGtfs(configuration.getGtfsServiceFilename()), bulk);
	}

	public void importService(String fileName, int bulk) {
		ratpGtfsServiceCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importServiceCalendar(int bulk) {
		ratpGtfsServiceCalendarCsvImport.importData(
				getGtfs(configuration.getGtfsServiceCalendarFilename()), bulk);
	}

	public void importServiceCalendar(String fileName, int bulk) {
		ratpGtfsServiceCalendarCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importRoute(int bulk) {
		ratpGtfsRouteCsvImport.importData(
				getGtfs(configuration.getGtfsRouteFilename()), bulk);
	}

	public void importRoute(String fileName, int bulk) {
		ratpGtfsRouteCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importStop(int bulk) {
		ratpGtfsStopCsvImport.importData(
				getGtfs(configuration.getGtfsStopFilename()), bulk);
	}

	public void importStop(String fileName, int bulk) {
		ratpGtfsStopCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importStopTransfert(int bulk) {
		ratpGtfsStopTransfertCsvImport.importData(
				getGtfs(configuration.getGtfsTransfertFilename()), bulk);
	}

	public void importStopTransfert(String fileName, int bulk) {
		ratpGtfsStopTransfertCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importStopTime(int bulk) {
		ratpGtfsStopTimeCsvImport.importData(
				getGtfs(configuration.getGtfsStopTimeFilename()), bulk);
	}

	public void importStopTime(String fileName, int bulk) {
		ratpGtfsStopTimeCsvImport.importData(getGtfs(fileName), bulk);
	}

	public void importStopAccessibility(String fileName, int bulk) {
		ratpGtfsStopAccessibilityCsvImport.importData(
				getAccessibility(fileName), bulk);
	}

	public void importRouteAccessibility(String fileName, int bulk) {
		ratpGtfsRouteAccessibilityCsvImport.importData(
				getAccessibility(fileName), bulk);
	}

}
