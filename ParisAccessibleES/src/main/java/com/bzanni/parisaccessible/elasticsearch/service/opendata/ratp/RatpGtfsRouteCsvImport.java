package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsRouteRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsRouteCsvImport extends GenericCsvImporter<RatpGtfsRoute> {

	@Resource
	private RatpGtfsRouteRepository repository;

	@Override
	protected List<RatpGtfsRoute> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}
		String id = list[0];
		if (id != null && !id.equals("")) {

			if (!list[5].equals("")) {
				RatpGtfsRoute prepare = repository.prepare(id);
				prepare.setName(list[2]);
				prepare.setLongName(list[3]);
				prepare.setDescription(list[4]);
				prepare.setType(Integer.valueOf(list[5]));
				prepare.setRoute_color(list[7]);
				prepare.setRoute_color_text(list[8]);
				List<RatpGtfsRoute> res = new ArrayList<RatpGtfsRoute>();
				res.add(prepare);
				return res;
			}
		}
		return null;
	}

	@Override
	protected void savePack(List<RatpGtfsRoute> pack) throws Exception {
		repository.save(pack);
	}

	@Override
	public char delimiter() {
		return ',';
	}

}
