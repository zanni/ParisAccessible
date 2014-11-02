package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsServiceRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsServiceCsvImport extends
		GenericCsvImporter<RatpGtfsService> {

	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyyMMdd");
	@Resource
	private RatpGtfsServiceRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsService> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}

		String id = list[0];

		if (id != null && !id.equals("")) {

			if (!list[0].equals("")) {
				List<RatpGtfsService> res = new ArrayList<RatpGtfsService>();
				RatpGtfsService prepare = repository.prepare(list[0]);
				prepare.setStartDate(format.parse(list[8]));
				prepare.setEndDate(format.parse(list[9]));
				res.add(prepare);
				return res;
			}
		}
		return null;
	}

	@Override
	protected void savePack(List<RatpGtfsService> pack) throws Exception {
		repository.save(pack);
	}

}
