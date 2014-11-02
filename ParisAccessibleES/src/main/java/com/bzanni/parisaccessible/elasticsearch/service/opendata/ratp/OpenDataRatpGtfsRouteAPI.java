package com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsRouteRepository;

@Service
@Configurable
public class OpenDataRatpGtfsRouteAPI {

	private final static int BULK_PACK = 100;

	@Value("${inject_path}")
	private String path;

	@Resource
	private RatpGtfsRouteRepository busLineRepository;

	public void grabData() {
		System.out.println("Grab Routes");
		String csvFile = path + "gtfs/routes.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int rejected = 0;
		int imported = 0;
		boolean first = true;
		int i = 0;
		try {
			List<RatpGtfsRoute> res = new ArrayList<RatpGtfsRoute>();
			br = new BufferedReader(new FileReader(csvFile));
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

					if (!list[5].equals("")) {
						RatpGtfsRoute prepare = busLineRepository.prepare(id);
						prepare.setName(list[2]);
						prepare.setLongName(list[3]);
						prepare.setDescription(list[4]);
						prepare.setType(Integer.valueOf(list[5]));
						prepare.setRoute_color(list[7]);
						prepare.setRoute_color_text(list[8]);
						res.add(prepare);
						i++;
						if (i % OpenDataRatpGtfsRouteAPI.BULK_PACK == 0) {

							busLineRepository.save(res);
							System.out.println("imported " + res.size()
									+ " lines");
							imported += res.size();
							res = new ArrayList<RatpGtfsRoute>();
						}
					} else
						rejected++;

				} else
					rejected++;

			}

			busLineRepository.save(res);
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

	public void populateBusRouteAccessibility() {
		System.out.println("populate Routes accessibility");
		String csvFile = path + "bus_access.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		int imported = 0;
		int i = 0;
		try {
			List<RatpGtfsRoute> res = new ArrayList<RatpGtfsRoute>();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] list = line.split(cvsSplitBy);
				if (list.length < 8)
					continue;

				List<RatpGtfsRoute> findByShortName = busLineRepository
						.findByShortName(list[1]);
				if (findByShortName == null)
					continue;
				for (RatpGtfsRoute prepare : findByShortName) {
					prepare.setName(list[1]);
					prepare.setOrigin(list[2]);
					prepare.setDestination(list[3]);
					if (list[4].equals("1")) {
						prepare.setAccessibleUFR(true);
					} else {
						prepare.setAccessibleUFR(false);
					}
					if (list[5].equals("1")) {
						prepare.setPlancherBas(true);
					} else {
						prepare.setPlancherBas(false);
					}
					if (list[6].equals("1")) {
						prepare.setPalette(true);
					} else {
						prepare.setPalette(false);
					}
					if (list[7].equals("1")) {
						prepare.setAnnonceSonoreProchainArret(true);
					} else {
						prepare.setAnnonceSonoreProchainArret(false);
					}
					if (list[8].equals("1")) {
						prepare.setAnnonceVisuelleProchainArret(true);
					} else {
						prepare.setAnnonceVisuelleProchainArret(false);
					}
					res.add(prepare);
					i++;
					if (i % OpenDataRatpGtfsRouteAPI.BULK_PACK == 0) {

						busLineRepository.save(res);
						System.out.println("imported " + res.size() + " lines");
						imported += res.size();
						res = new ArrayList<RatpGtfsRoute>();
					}
				}
			}

			busLineRepository.save(res);
			imported += res.size();
			System.out.println("imported " + res.size() + " lines");

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

		System.out.println("total: " + imported + " updated");

	}

	public void grabDataStop() {

	}
}
