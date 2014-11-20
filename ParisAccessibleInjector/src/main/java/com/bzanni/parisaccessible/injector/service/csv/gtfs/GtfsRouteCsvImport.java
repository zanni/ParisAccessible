package com.bzanni.parisaccessible.injector.service.csv.gtfs;

import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsRouteRepository;
import com.bzanni.parisaccessible.injector.service.util.GenericCsvImporter;

@Service
public class GtfsRouteCsvImport extends GenericCsvImporter<GtfsRoute> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsRouteCsvImport.class);

	public Logger getLogger() {
		return GtfsRouteCsvImport.LOGGER;
	}
	@Resource
	private GtfsRouteRepository repository;

	@Override
	protected List<GtfsRoute> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}
		String id = list[0];
		if (id != null && !id.equals("")) {

			if (!list[5].equals("")) {
				GtfsRoute prepare = new GtfsRoute();
				prepare.setId(id);
				prepare.setName(list[2]);
				prepare.setLongName(list[3]);
				prepare.setDescription(list[4]);
				prepare.setType(Integer.valueOf(list[5]));
				prepare.setRoute_color(list[7]);
				prepare.setRoute_color_text(list[8]);
				List<GtfsRoute> res = new ArrayList<GtfsRoute>();
				res.add(prepare);
				return res;
			}
		}
		return null;
	}

	@Override
	protected void savePack(List<GtfsRoute> pack) throws Exception {
		repository.save(pack);
	}

	@Override
	public char delimiter() {
		return ',';
	}

}
