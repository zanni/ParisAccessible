package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.opendataparis.PassagePietonRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.opendataparis.TrottoirRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class OpenDataParisPassagePietonAPI {

	private final static String SERVICE_URL = "http://parisdata.opendatasoft.com/api/records/1.0/search?dataset=mobiliers_et_emprises_au_sol_de_signalisation_routiere_et_pietonne_-_donnees_geo&q=PVPPAPI";

	private final static int BULK_PACK = 500;

	private static final String distance = "1m";

	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private PassagePietonRepository passagePietonRepository;

	private class UrlBuilder {
		private String url;

		public UrlBuilder() {
			url = OpenDataParisPassagePietonAPI.SERVICE_URL;
		}

		public UrlBuilder withStart(int start) {
			url += "&start=" + start;
			return this;
		}

		public UrlBuilder withRows(int rows) {
			url += "&rows=" + rows;
			return this;
		}

		public String build() {
			return url;
		}
	}

	private void importRecords(JsonElement records, int total, int current) {
		JsonArray asJsonArray = records.getAsJsonArray();
		List<PassagePieton> list = new ArrayList<PassagePieton>();
		for (int i = 0; i < asJsonArray.size(); i++) {
			JsonElement jsonElement = asJsonArray.get(i);
			JsonObject record = jsonElement.getAsJsonObject();

			JsonObject fields = record.get("fields").getAsJsonObject();

			String asString = fields.get("geometry").getAsString();
			if (asString.indexOf("LineString") >= 0) {
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
						if (passage.getStart() != null
								&& passage.getEnd() != null) {
							Double start = 0D;
							Double end = 0D;

							try {
								start = trottoirRepository.count(passage
										.getStart().getLat(), passage
										.getStart().getLon(), distance);
								end = trottoirRepository.count(passage.getEnd()
										.getLat(), passage.getEnd().getLon(),
										distance);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (start > 0 || end > 0) {
								list.add(passage);
								System.out.println("matches: start:" + start
										+ " end:" + end);
							}
						}
					}
				}

			}
		}

		try {
			passagePietonRepository.save(list);
			System.out.println("studied: " + current + "/" + total
					+ " : sutdied " + asJsonArray.size() + " imported "
					+ list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	private int grabPart(UrlBuilder builder, int current) {

		HttpGet request = new HttpGet(builder.build());
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				Gson gson = new Gson();

				JsonElement fromJson = gson.fromJson(rd, JsonElement.class);

				if (fromJson != null && fromJson.getAsJsonObject() != null) {

					JsonObject asJsonObject = fromJson.getAsJsonObject();
					if (asJsonObject != null) {
						JsonElement nhits = asJsonObject.get("nhits");
						JsonElement records = asJsonObject.get("records");
						importRecords(records, nhits.getAsInt(), current);
						return nhits.getAsInt();
					}

				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public void grabData() {
		UrlBuilder builder = new UrlBuilder();
		builder.withRows(OpenDataParisPassagePietonAPI.BULK_PACK);

		int total = grabPart(builder, 0);
		int current = OpenDataParisPassagePietonAPI.BULK_PACK;

		while (current < total) {
			builder = new UrlBuilder();
			builder.withRows(OpenDataParisPassagePietonAPI.BULK_PACK);
			current += OpenDataParisPassagePietonAPI.BULK_PACK;
			builder.withStart(current);
			grabPart(builder, current);
		}

	}

}
