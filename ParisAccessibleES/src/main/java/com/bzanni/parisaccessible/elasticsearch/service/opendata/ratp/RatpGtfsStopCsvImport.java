package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsStopCsvImport extends GenericCsvImporter<RatpGtfsStop> {

	@Resource
	private RatpGtfsStopRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsStop> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}
		String id = list[0];
		List<RatpGtfsStop> res = new ArrayList<RatpGtfsStop>();
		RatpGtfsStop stop = new RatpGtfsStop();

		stop.setId(id);
		stop.setName(list[2]);
		stop.setDescription(list[3]);
		stop.setLocation(new GeoPoint(Double.valueOf(list[4]), Double
				.valueOf(list[5])));

		res.add(stop);

		return res;
	}

	@Override
	protected void savePack(List<RatpGtfsStop> pack) throws Exception {
		repository.save(pack);
	}

}
