package com.bzanni.parisaccessible.injector.service.csv.gtfs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsTripRepository;
import com.bzanni.parisaccessible.injector.service.util.GenericCsvImporter;

@Service
public class GtfsTripCsvImport extends GenericCsvImporter<GtfsTrip> {
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsTripCsvImport.class);

	public Logger getLogger() {
		return GtfsTripCsvImport.LOGGER;
	}
	
	@Resource
	private GtfsTripRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsTrip> convert(String[] list) throws Exception {
		if (list.length < 6) {
			return null;
		}
		String id = list[0];

		if (id != null && !id.equals("")) {

			if (!list[0].equals("")) {
				List<GtfsTrip> res = new ArrayList<GtfsTrip>();
				GtfsTrip prepare = new GtfsTrip();
				prepare.setId(list[2]);
				prepare.setRoute_id(list[0]);
				prepare.setService_id(list[1]);

				res.add(prepare);
				return res;
			}

		}
		return null;
	}

	@Override
	protected void savePack(List<GtfsTrip> pack) throws Exception {
		repository.save(pack);
	}

}
