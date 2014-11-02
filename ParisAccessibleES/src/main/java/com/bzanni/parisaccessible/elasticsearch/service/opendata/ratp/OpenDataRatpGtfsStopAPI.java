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

import au.com.bytecode.opencsv.CSVReader;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.Lambert;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.LambertPoint;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.LambertZone;
import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsStop;
import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpGtfsTripRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ratp.RatpStopTimeRepository;

@Service
public class OpenDataRatpGtfsStopAPI {

	private final static int BULK_PACK = 1000;

	@Value("${inject_path}")
	private String path;

	@Resource
	private RatpGtfsStopRepository ratpRouteStopRepository;

	@Resource
	private RatpGtfsTripRepository ratpTripRepository;
	
	@Resource
	private RatpStopTimeRepository ratpStopTimeRepository;

	public void grabData() {
		System.out.println("grab Stop");
		String csvFile = path + "gtfs/stops.txt";
		BufferedReader br = null;
		int rejected = 0;
		int imported = 0;
		boolean first = true;
		int i = 0;
		try {
			List<RatpGtfsStop> res = new ArrayList<RatpGtfsStop>();
			CSVReader reader = new CSVReader(new FileReader(csvFile), ',');
			String[] list;

			while ((list = reader.readNext()) != null) {

				if (first) {
					first = false;
					continue;
				}

				if (list.length < 8)
					continue;
				String id = list[0];

				RatpGtfsStop stop = new RatpGtfsStop();

				stop.setId(id);
				stop.setName(list[2]);
				stop.setDescription(list[3]);
				stop.setLocation(new GeoPoint(Double.valueOf(list[4]), Double
						.valueOf(list[5])));

				res.add(stop);

				i++;
				if (i % OpenDataRatpGtfsStopAPI.BULK_PACK == 0) {

					ratpRouteStopRepository.save(res);
					System.out.println("imported " + res.size() + " lines");
					imported += res.size();
					res = new ArrayList<RatpGtfsStop>();
				}

			}

			ratpRouteStopRepository.save(res);
			System.out.println("imported " + res.size() + " lines");
			imported += res.size();
			reader.close();

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

	public void populateAccessibility() {
		System.out.println("populate Stop accessibility");
		String csvFile = path + "stops_acessibility.csv";
		BufferedReader br = null;
		int rejected = 0;
		int imported = 0;
		boolean first = true;
		int i = 0;
		try {
			List<RatpGtfsStop> res = new ArrayList<RatpGtfsStop>();
			CSVReader reader = new CSVReader(new FileReader(csvFile), ';');
			String[] list;

			while ((list = reader.readNext()) != null) {

				if (first) {
					first = false;
					continue;
				}

				if (list.length < 8)
					continue;
				try {
					Double v1 = Double.valueOf(list[4]);
					Double v2 = Double.valueOf(list[5]);
					//
					// res.add(stop);

					LambertPoint pt = Lambert.convertToWGS84Deg(v1, v2,
							LambertZone.LambertIIExtended);

					List<RatpGtfsStop> search = ratpRouteStopRepository.search(
							pt.getY(), pt.getX(), "10m");
					for (RatpGtfsStop stop : search) {
						boolean bool = (list[6].equals("1")) ? true : false;
						stop.setAccessibleUFR(bool);
						bool = (list[7].equals("1")) ? true : false;
						stop.setAnnonceSonorProchainArret(bool);
						bool = (list[8].equals("1")) ? true : false;
						stop.setAnnonceVisuelleProchainArret(bool);
						bool = (list[9].equals("1")) ? true : false;
						stop.setAnnonceSonoreSituationPerturbe(bool);
						bool = (list[10].equals("1")) ? true : false;
						stop.setAnnonceVisuelleSituationPerturbe(bool);

						res.add(stop);
						i++;

						if (i % OpenDataRatpGtfsStopAPI.BULK_PACK == 0) {

							ratpRouteStopRepository.save(res);
							System.out.println("imported " + res.size()
									+ " lines");
							imported += res.size();
							res = new ArrayList<RatpGtfsStop>();
						}

					}
				} catch (NumberFormatException e) {

				}

			}

			ratpRouteStopRepository.save(res);
			System.out.println("imported " + res.size() + " lines");
			imported += res.size();
			reader.close();

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

	public void populateLinks() {
		String csvFile = path + "gtfs/stop_times.txt";

		boolean first = true;
		int i = 0;
		int j = 0;
		int total = 0;
		try {
			List<RatpGtfsStop> res = new ArrayList<RatpGtfsStop>();
			CSVReader reader = new CSVReader(new FileReader(csvFile), ',');
			String[] list;

			while ((list = reader.readNext()) != null) {

				if (first) {
					first = false;
					continue;
				}

				RatpGtfsTrip trip = ratpTripRepository.findById(list[0]);

				RatpGtfsStop stop = ratpRouteStopRepository.findById(list[3]);

				if (stop != null && trip != null) {
					try {
						Integer valueOf = Integer.valueOf(list[4]);
						stop.getConnections().put(trip.getRoute_id(), valueOf);
						
						res.add(stop);
						i++;

						if (i % OpenDataRatpGtfsStopAPI.BULK_PACK == 0) {

							ratpRouteStopRepository.save(res);
							System.out.println("updated " + res.size()
									+ " lines");
							res = new ArrayList<RatpGtfsStop>();
						}

					} catch (NumberFormatException e) {

					}
				}
				
				if (j % 10000 == 0) {

					System.out.println("studied " + j
							+ " lines");
					res = new ArrayList<RatpGtfsStop>();
				}

			}

			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
