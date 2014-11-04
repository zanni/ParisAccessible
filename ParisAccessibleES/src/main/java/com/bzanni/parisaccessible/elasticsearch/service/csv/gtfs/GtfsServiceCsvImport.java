package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsServiceRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsServiceCsvImport extends
		GenericCsvImporter<GtfsService> {

	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyyMMdd");
	@Resource
	private GtfsServiceRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsService> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}

		String id = list[0];

		if (id != null && !id.equals("")) {

			if (!list[0].equals("")) {
				List<GtfsService> res = new ArrayList<GtfsService>();
				GtfsService prepare = new GtfsService();
				prepare.setId(list[0]);
				prepare.setStartDate(format.parse(list[8]));
				prepare.setEndDate(format.parse(list[9]));
				res.add(prepare);
				return res;
			}
		}
		return null;
	}

	@Override
	protected void savePack(List<GtfsService> pack) throws Exception {
		repository.save(pack);
	}

}
