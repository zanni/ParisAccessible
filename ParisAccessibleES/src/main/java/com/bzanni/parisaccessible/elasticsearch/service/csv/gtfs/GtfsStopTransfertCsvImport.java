package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsStopTransfertCsvImport extends
		GenericCsvImporter<GtfsStop> {

	@Resource
	private GtfsStopRepository repository;

	private Map<String, GtfsStop> cache = new HashMap<String, GtfsStop>();

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsStop> convert(String[] line) throws Exception {
		if (line.length < 4) {
			return null;
		}
		String id = line[0];

		GtfsStop ratpGtfsStop = cache.get(id);

		if (ratpGtfsStop == null) {
			ratpGtfsStop = repository.findById(id);
			if (ratpGtfsStop != null) {
				cache.put(id, ratpGtfsStop);
			}
		}
		if (ratpGtfsStop != null) {
			ratpGtfsStop
					.addTransfert(line[1], line[2], Double.valueOf(line[3]));
			List<GtfsStop> res = new ArrayList<GtfsStop>();
			res.add(ratpGtfsStop);
			return res;
		}

		return null;
	}

	@Override
	protected void savePack(List<GtfsStop> pack) throws Exception {
		repository.save(pack);
	}

}
