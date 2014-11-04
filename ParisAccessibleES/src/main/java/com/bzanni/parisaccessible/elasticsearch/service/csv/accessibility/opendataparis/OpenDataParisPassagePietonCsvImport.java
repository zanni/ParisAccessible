package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.PassagePietonRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

/**
 * 
 * geometry;geometry_vertex_count;info;libelle;info_ft_style;libelle_ft_style;
 * import_notes
 * 
 * @author bertrandzanni
 *
 */
@Service
public class OpenDataParisPassagePietonCsvImport extends
		GenericCsvImporter<PassagePieton> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenDataParisPassagePietonCsvImport.class);

	public Logger getLogger() {
		return OpenDataParisPassagePietonCsvImport.LOGGER;
	}
	
//	private final static String PASSAGEPIETON_TAG = "PVPPAPI";

	@Resource
	private PassagePietonRepository passagePietonRepository;

	@Override
	public char delimiter() {
		return ';';
	}

	@Override
	protected List<PassagePieton> convert(String[] line) throws Exception {
		if (line.length < 6) {
			return null;
		}
		
//		if(line[2] != OpenDataParisPassagePietonCsvImport.PASSAGEPIETON_TAG){
//			return null;
//		}
//		
		List<PassagePieton> res = null;
		String asString = line[0];
		if (asString.indexOf("LineString") >= 0) {
			res = new ArrayList<PassagePieton>();
			String[] split = asString.split("<coordinates>");
			String[] split2 = split[1].split("</coordinates>");

			String string = split2[0];

			String[] split3 = string.split(" ");
			if (split3.length == 2) {
				PassagePieton passage = new PassagePieton();
				for (String j : split3) {
					String[] split4 = j.split(",");
					Double v1 = Double.valueOf(split4[0]);
					Double v2 = Double.valueOf(split4[1]);

					if (passage.getStart() == null) {
						passage.setStart(new GeoPoint(v1, v2));
					} else {
						passage.setEnd(new GeoPoint(v1, v2));
					}
					if (passage.getStart() != null && passage.getEnd() != null) {

						passage.setInfo(line[2]);
						passage.setLibelle(line[3]);
						passage.setLibelle_ft_style(line[4]);
						passage.setImport_notes(line[5]);

						res.add(passage);
					}

				}
			}
		}
		return res;
	}

	@Override
	protected void savePack(List<PassagePieton> pack) throws Exception {
		passagePietonRepository.save(pack);
	}

}
