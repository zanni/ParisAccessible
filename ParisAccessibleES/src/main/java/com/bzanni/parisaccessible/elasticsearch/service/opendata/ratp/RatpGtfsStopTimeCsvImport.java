package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsStopTime;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsStopTimeCsvImport extends
		GenericCsvImporter<RatpGtfsStopTime> {

	@Override
	public char delimiter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected List<RatpGtfsStopTime> convert(String[] line) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void savePack(List<RatpGtfsStopTime> pack) throws Exception {
		// TODO Auto-generated method stub

	}

}
