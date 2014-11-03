package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.gtfs.GtfsServiceRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsServiceCalendarCsvImport extends
		GenericCsvImporter<GtfsService> {

	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyyMMdd");
	@Resource
	private GtfsServiceRepository repository;

	private Map<String, GtfsService> cache = new HashMap<String, GtfsService>();

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsService> convert(String[] list) throws Exception {
		if (list.length < 3) {
			return null;
		}

		String id = list[0];

		GtfsService findById = cache.get(id);
		if (findById == null) {
			findById = repository.findById(id);
		}
		if (findById != null) {
			cache.put(id, findById);
			Date parse = format.parse(list[1]);
			findById.addCalendar(parse);
			List<GtfsService> res = new ArrayList<GtfsService>();
			res.add(findById);
		}
		return null;
	}

	@Override
	protected void savePack(List<GtfsService> pack) throws Exception {
		repository.save(pack);
	}

}
