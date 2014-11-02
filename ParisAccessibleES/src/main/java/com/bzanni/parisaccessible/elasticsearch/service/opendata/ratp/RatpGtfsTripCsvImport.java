package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsTripRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsTripCsvImport extends GenericCsvImporter<RatpGtfsTrip> {

	@Resource
	private RatpGtfsTripRepository repository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsTrip> convert(String[] list) throws Exception {
		if (list.length < 6) {
			return null;
		}
		String id = list[0];

		if (id != null && !id.equals("")) {

			if (!list[0].equals("")) {
				List<RatpGtfsTrip> res = new ArrayList<RatpGtfsTrip>();
				RatpGtfsTrip prepare = new RatpGtfsTrip();
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
	protected void savePack(List<RatpGtfsTrip> pack) throws Exception {
		repository.save(pack);
	}

}
