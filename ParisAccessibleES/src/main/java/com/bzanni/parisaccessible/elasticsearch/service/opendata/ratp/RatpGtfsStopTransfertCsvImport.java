package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsStopTransfertCsvImport extends
		GenericCsvImporter<RatpGtfsStop> {

	@Resource
	private RatpGtfsStopRepository repository;

	private Map<String, RatpGtfsStop> cache = new HashMap<String, RatpGtfsStop>();

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsStop> convert(String[] line) throws Exception {
		if (line.length < 4) {
			return null;
		}
		String id = line[0];

		RatpGtfsStop ratpGtfsStop = cache.get(id);

		if (ratpGtfsStop == null) {
			ratpGtfsStop = repository.findById(id);
			if (ratpGtfsStop != null) {
				cache.put(id, ratpGtfsStop);
			}
		}
		if (ratpGtfsStop != null) {
			ratpGtfsStop
					.addTransfert(line[1], line[2], Double.valueOf(line[3]));
			List<RatpGtfsStop> res = new ArrayList<RatpGtfsStop>();
			res.add(ratpGtfsStop);
			return res;
		}

		return null;
	}

	@Override
	protected void savePack(List<RatpGtfsStop> pack) throws Exception {
		repository.save(pack);
	}

}
