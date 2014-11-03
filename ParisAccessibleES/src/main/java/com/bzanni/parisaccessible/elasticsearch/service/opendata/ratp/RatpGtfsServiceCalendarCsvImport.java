package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsServiceRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsServiceCalendarCsvImport extends
		GenericCsvImporter<RatpGtfsService> {

	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyyMMdd");
	@Resource
	private RatpGtfsServiceRepository repository;

	private Map<String, RatpGtfsService> cache = new HashMap<String, RatpGtfsService>();

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsService> convert(String[] list) throws Exception {
		if (list.length < 3) {
			return null;
		}

		String id = list[0];

		RatpGtfsService findById = cache.get(id);
		if (findById == null) {
			findById = repository.findById(id);
		}
		if (findById != null) {
			cache.put(id, findById);
			Date parse = format.parse(list[1]);
			findById.addCalendar(parse);
			List<RatpGtfsService> res = new ArrayList<RatpGtfsService>();
			res.add(findById);
		}
		return null;
	}

	@Override
	protected void savePack(List<RatpGtfsService> pack) throws Exception {
		repository.save(pack);
	}

}
