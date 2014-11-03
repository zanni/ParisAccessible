package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.opendataparis.PassagePietonRepository;
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
						Double start = 0D;
						Double end = 0D;

						PassagePieton p = new PassagePieton();
						p.setInfo(line[2]);
						p.setLibelle(line[3]);
						p.setLibelle_ft_style(line[4]);
						p.setImport_notes(line[5]);

						if (start > 0 || end > 0) {
							res.add(passage);
						}
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
