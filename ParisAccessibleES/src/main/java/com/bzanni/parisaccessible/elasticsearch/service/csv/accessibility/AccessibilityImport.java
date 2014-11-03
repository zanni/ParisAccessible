package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis.OpenDataParisEquipementCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis.OpenDataParisPassagePietonCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis.OpenDataParisTrottoirCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataratp.RatpAccessibilityRouteCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataratp.RatpAccessibilityStopCsvImport;
import com.bzanni.parisaccessible.elasticsearch.service.util.ParisAccessibleConfigurationBean;

@Service
public class AccessibilityImport {

	@Resource
	private RatpAccessibilityRouteCsvImport ratpGtfsRouteAccessibilityCsvImport;

	@Resource
	private RatpAccessibilityStopCsvImport ratpGtfsStopAccessibilityCsvImport;

	@Resource
	private OpenDataParisPassagePietonCsvImport openDataParisPassagePietonCsvImport;

	@Resource
	private OpenDataParisEquipementCsvImport openDataParisEquipementCsvImport;

	@Resource
	private OpenDataParisTrottoirCsvImport openDataParisTrottoirCsvImport;

	@Resource
	private ParisAccessibleConfigurationBean configuration;

	private String getAccessibility(String fileName) {
		return configuration.getAccessibilityPath() + "/" + fileName;
	}

	public void importRoute(int bulk) {
		ratpGtfsRouteAccessibilityCsvImport
				.importData(getAccessibility(configuration
						.getAccessibilityRouteFilename()), bulk);
	}

	public void importRoute(String fileName, int bulk) {
		ratpGtfsRouteAccessibilityCsvImport.importData(
				getAccessibility(fileName), bulk);
	}

	public void importStop(int bulk) {
		ratpGtfsStopAccessibilityCsvImport.importData(
				getAccessibility(configuration.getAccessibilityStopFilename()),
				bulk);
	}

	public void importStop(String fileName, int bulk) {
		ratpGtfsStopAccessibilityCsvImport.importData(
				getAccessibility(fileName), bulk);
	}

	public void importTrottoir(int bulk) {
		openDataParisTrottoirCsvImport.importData(
				getAccessibility(configuration.getTrottoirFilename()), bulk);
	}

	public void importTrottoir(String fileName, int bulk) {
		openDataParisTrottoirCsvImport.importData(getAccessibility(fileName),
				bulk);
	}

	public void importEquipement(int bulk) {
		openDataParisEquipementCsvImport.importData(
				getAccessibility(configuration.getEquipementFilename()), bulk);
	}

	public void importEquipement(String fileName, int bulk) {
		openDataParisEquipementCsvImport.importData(getAccessibility(fileName),
				bulk);
	}

	public void importPassagePieton(int bulk) {
		openDataParisPassagePietonCsvImport.importData(
				getAccessibility(configuration.getPassagePietonFilename()),
				bulk);
	}

	public void importPassagePieton(String fileName, int bulk) {
		openDataParisPassagePietonCsvImport.importData(
				getAccessibility(fileName), bulk);
	}

}
