package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsAgency;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsAgencyRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpGtfsAgencyCsvImport extends GenericCsvImporter<RatpGtfsAgency> {

	@Resource
	private RatpGtfsAgencyRepository ratpGtfsAgencyRepository;

	@Override
	public char delimiter() {
		return ',';
	}

	@Override
	protected List<RatpGtfsAgency> convert(String[] list) throws Exception {
		if (list.length < 6) {
			return null;
		}
		String id = list[0];
		List<RatpGtfsAgency> res = new ArrayList<RatpGtfsAgency>();
		RatpGtfsAgency agency = ratpGtfsAgencyRepository.prepare(id);
		agency.setName(list[1]);
		agency.setUrl(list[2]);
		agency.setTimezone(list[3]);
		agency.setLang(list[4]);
		agency.setPhone(list[5]);
		res.add(agency);

		return res;
	}

	@Override
	protected void savePack(List<RatpGtfsAgency> pack) throws Exception {
		ratpGtfsAgencyRepository.save(pack);
	}

}
