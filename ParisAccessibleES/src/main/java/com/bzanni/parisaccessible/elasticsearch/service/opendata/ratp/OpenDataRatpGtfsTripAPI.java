package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsTripRepository;

@Service
public class OpenDataRatpGtfsTripAPI {

	private final static int BULK_PACK = 10000;

	@Value("${inject_path}")
	private String path;

	@Resource
	private RatpGtfsTripRepository repository;

	public void grabData() {
		System.out.println("grab trips");
		String csvFile = path + "gtfs/trips.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int rejected = 0;
		int imported = 0;
		boolean first = true;
		int i = 0;
		try {
			List<RatpGtfsTrip> res = new ArrayList<RatpGtfsTrip>();
			br = new BufferedReader(new FileReader(csvFile));

			while ((line = br.readLine()) != null) {

				if (first) {
					first = false;
					continue;
				}

				String[] list = line.split(cvsSplitBy);
				if (list.length < 6)
					continue;
				String id = list[0];

				if (id != null && !id.equals("")) {

					if (!list[0].equals("")) {
						RatpGtfsTrip prepare = new RatpGtfsTrip();
						prepare.setId(list[2]);
						prepare.setRoute_id(list[0]);
						prepare.setService_id(list[1]);

						res.add(prepare);
						i++;
						if (i % OpenDataRatpGtfsTripAPI.BULK_PACK == 0) {

							repository.save(res);
							System.out.println("imported " + res.size()
									+ " lines");
							imported += res.size();
							res = new ArrayList<RatpGtfsTrip>();
						}
					} else
						rejected++;

				} else
					rejected++;

			}

			repository.save(res);
			System.out.println("imported " + res.size() + " lines");
			imported += res.size();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("total: " + imported + "imported with " + rejected
				+ " rejected");
	}

}
