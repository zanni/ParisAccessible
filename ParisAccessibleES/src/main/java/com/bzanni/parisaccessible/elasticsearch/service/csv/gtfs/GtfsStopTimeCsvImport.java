package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStopTime;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsStopTimeRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsStopTimeCsvImport extends GenericCsvImporter<GtfsStopTime> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsStopTimeCsvImport.class);

	public Logger getLogger() {
		return GtfsStopTimeCsvImport.LOGGER;
	}

	@Resource
	private GtfsStopTimeRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsStopTime> convert(String[] line) throws Exception {
		GtfsStopTime gtfsStopTime = new GtfsStopTime();
		gtfsStopTime.setTrip_id(line[0]);
		gtfsStopTime.setTime(line[1]);
		gtfsStopTime.setStop_id(line[3]);
		gtfsStopTime.setSeq(line[4]);
		return Arrays.asList(gtfsStopTime);
	}

	@Override
	protected void savePack(List<GtfsStopTime> pack) throws Exception {
		repository.save(pack);
	}

}
