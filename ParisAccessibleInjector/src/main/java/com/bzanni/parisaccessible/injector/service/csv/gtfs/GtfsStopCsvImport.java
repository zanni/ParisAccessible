package com.bzanni.parisaccessible.injector.service.csv.gtfs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsStopRepository;
import com.bzanni.parisaccessible.injector.service.util.GenericCsvImporter;
import com.bzanni.parisaccessible.injector.service.util.MemcachedService;

@Service
public class GtfsStopCsvImport extends GenericCsvImporter<GtfsStop> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsStopCsvImport.class);

	public Logger getLogger() {
		return GtfsStopCsvImport.LOGGER;
	}
	
	@Resource
	private GtfsStopRepository repository;
	

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsStop> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}
		String id = list[0];
		List<GtfsStop> res = new ArrayList<GtfsStop>();
		GtfsStop stop = new GtfsStop();

		stop.setId(id);
		stop.setName(list[2]);
		stop.setDescription(list[3]);
		stop.setLocation(new GeoPoint(Double.valueOf(list[4]), Double
				.valueOf(list[5])));

		res.add(stop);

		return res;
	}

	@Override
	protected void savePack(List<GtfsStop> pack) throws Exception {
	
		repository.save(pack);
	}

}
