package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsServiceRepository;

@Service
public class OpenDataRatpGtfsServiceAPI {

	private final static int BULK_PACK = 100;

	@Value("${inject_path}")
	private String path;

	@Resource
	private RatpGtfsServiceRepository repository;

	public void grabData() {
		System.out.println("grab Service");
		String csvFile = path + "gtfs/calendar.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int rejected = 0;
		int imported = 0;
		boolean first = true;
		int i = 0;
		try {
			List<RatpGtfsService> res = new ArrayList<RatpGtfsService>();
			br = new BufferedReader(new FileReader(csvFile));
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			while ((line = br.readLine()) != null) {

				if (first) {
					first = false;
					continue;
				}

				String[] list = line.split(cvsSplitBy);
				if (list.length < 8)
					continue;
				String id = list[0];

				if (id != null && !id.equals("")) {

					if (!list[0].equals("")) {
						RatpGtfsService prepare = repository.prepare(list[0]);
						prepare.setStartDate(format.parse(list[8]));
						prepare.setEndDate(format.parse(list[9]));
						res.add(prepare);
						i++;
						if (i % OpenDataRatpGtfsServiceAPI.BULK_PACK == 0) {

							repository.save(res);
							System.out.println("imported " + res.size()
									+ " lines");
							imported += res.size();
							res = new ArrayList<RatpGtfsService>();
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
