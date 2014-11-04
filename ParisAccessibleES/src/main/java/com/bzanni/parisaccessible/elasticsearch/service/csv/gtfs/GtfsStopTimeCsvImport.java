package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStopTime;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsStopTimeCsvImport extends
		GenericCsvImporter<GtfsStopTime> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsStopTimeCsvImport.class);

	public Logger getLogger() {
		return GtfsStopTimeCsvImport.LOGGER;
	}
	
	@Override
	public char delimiter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected List<GtfsStopTime> convert(String[] line) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void savePack(List<GtfsStopTime> pack) throws Exception {
		// TODO Auto-generated method stub

	}

}
