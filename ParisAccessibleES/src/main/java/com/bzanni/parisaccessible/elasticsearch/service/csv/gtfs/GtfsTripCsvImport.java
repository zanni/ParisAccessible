package com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.gtfs.GtfsTripRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class GtfsTripCsvImport extends GenericCsvImporter<GtfsTrip> {
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
