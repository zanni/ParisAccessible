package com.bzanni.parisaccessible.elasticsearch.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParisAccessibleConfigurationBean {

	@Value("${ratp_gtfs_index_name}")
	private String ratpGtfsIndexName;

	@Value("${inject_path}")
	private String injectPath;
	
	@Value("${gtfs_trip_filename}")
	private String gtfsTripFilename;
	
	@Value("${gtfs_service_filename}")
	private String gtfsServiceFilename;
	
	@Value("${gtfs_service_calendar_filename}")
	private String gtfsServiceCalendarFilename;
	
	@Value("${gtfs_stop_filename}")
	private String gtfsStopFilename;
	
	@Value("${gtfs_stoptime_filename}")
	private String gtfsStopTimeFilename;
	
	@Value("${gtfs_route_filename}")
	private String gtfsRouteFilename;
	
	@Value("${gtfs_agency_filename}")
	private String gtfsAgencyFilename;
	
	@Value("${gtfs_transfert_filename}")
	private String gtfsTransfertFilename;
	
	@Value("${accessibility_opendataparis_trottoir_filename}")
	private String trottoirFilename;
	
	@Value("${accessibility_opendataparis_equipement_filename}")
	private String equipementFilename;
	
	@Value("${accessibility_opendataparis_passagepieton_filename}")
	private String passagePietonFilename;
	
	@Value("${accessibility_opendataratp_route_filename}")
	private String accessibilityRouteFilename;
	
	@Value("${accessibility_opendataratp_stop_filename}")
	private String accessibilityStopFilename;

	public String getGtfsTripFilename() {
		return gtfsTripFilename;
	}

	public String getGtfsServiceFilename() {
		return gtfsServiceFilename;
	}

	public String getGtfsStopFilename() {
		return gtfsStopFilename;
	}

	public String getGtfsStopTimeFilename() {
		return gtfsStopTimeFilename;
	}

	public String getGtfsRouteFilename() {
		return gtfsRouteFilename;
	}

	public String getGtfsAgencyFilename() {
		return gtfsAgencyFilename;
	}

	public String getGtfsTransfertFilename() {
		return gtfsTransfertFilename;
	}

	public String getRatpGtfsIndexName() {
		return ratpGtfsIndexName;
	}

	public String getInjectPath() {
		return injectPath;
	}

	public String getGtfsServiceCalendarFilename() {
		return gtfsServiceCalendarFilename;
	}
	
	public String getGtfsPath(){
		return injectPath+"/gtfs";
	}
	
	public String getAccessibilityPath(){
		return injectPath+"/accessibility";
	}

	public String getTrottoirFilename() {
		return trottoirFilename;
	}

	public String getEquipementFilename() {
		return equipementFilename;
	}

	public String getPassagePietonFilename() {
		return passagePietonFilename;
	}

	public String getAccessibilityRouteFilename() {
		return accessibilityRouteFilename;
	}

	public String getAccessibilityStopFilename() {
		return accessibilityStopFilename;
	}
}
