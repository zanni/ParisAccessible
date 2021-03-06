package com.bzanni.parisaccessible.injector.service.csv.gtfs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsAgency;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsAgencyRepository;
import com.bzanni.parisaccessible.injector.service.util.GenericCsvImporter;

@Service
public class GtfsAgencyCsvImport extends GenericCsvImporter<GtfsAgency> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GtfsAgencyCsvImport.class);

	public Logger getLogger() {
		return GtfsAgencyCsvImport.LOGGER;
	}

	@Resource
	private GtfsAgencyRepository ratpGtfsAgencyRepository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<GtfsAgency> convert(String[] list) throws Exception {
		if (list.length < 6) {
			return null;
		}
		String id = list[0];
		List<GtfsAgency> res = new ArrayList<GtfsAgency>();
		GtfsAgency agency = ratpGtfsAgencyRepository.prepare(id);
		agency.setName(list[1]);
		agency.setUrl(list[2]);
		agency.setTimezone(list[3]);
		agency.setLang(list[4]);
		agency.setPhone(list[5]);
		res.add(agency);

		return res;
	}

	@Override
	protected void savePack(List<GtfsAgency> pack) throws Exception {
		ratpGtfsAgencyRepository.save(pack);
	}

}
