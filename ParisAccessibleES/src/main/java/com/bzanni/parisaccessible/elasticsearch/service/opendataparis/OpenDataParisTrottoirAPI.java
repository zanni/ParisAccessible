package com.bzanni.parisaccessible.elasticsearch.service.opendataparis;

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

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.business.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.TrottoirRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@Service
public class OpenDataParisTrottoirAPI {

	private final static String SERVICE_URL = "http://parisdata.opendatasoft.com/api/records/1.0/search?dataset=trottoirs_des_rues_de_paris";

	private final static int BULK_PACK = 500;

	@Resource
	private TrottoirRepository trottoirRepository;

	private class UrlBuilder {
		private String url;

		public UrlBuilder() {
			url = OpenDataParisTrottoirAPI.SERVICE_URL;
		}

		public UrlBuilder withStart(int start) {
			url += "&start=" + start;
			return this;
		}

		public UrlBuilder withRows(int rows) {
			url += "&rows=" + rows;
			return this;
		}

		public UrlBuilder withGeodistanceFilter(Double latitude,
				Double longitude, int distance_meter) {
			url += "&geofilter.distance=" + latitude + "," + longitude + ","
					+ distance_meter;
			return this;
		}

		public String build() {
			return url;
		}
	}

	private void importRecords(JsonElement records) {
		int fail = 0;
		JsonArray asJsonArray = records.getAsJsonArray();
		List<Trottoir> list = new ArrayList<Trottoir>();
		for (int i = 0; i < asJsonArray.size(); i++) {
			JsonElement jsonElement = asJsonArray.get(i);
			JsonObject record = jsonElement.getAsJsonObject();

			String id = record.get("recordid").getAsString();
			JsonObject fields = record.get("fields").getAsJsonObject();

			if (id != null && fields != null) {

				Trottoir trottoir = new Trottoir();
				trottoir.setId(id);
				if (fields.get("info") != null) {
					trottoir.setInfo(fields.get("info").getAsString());
				}
				if (fields.get("dist") != null) {
					trottoir.setDist(fields.get("dist").getAsString());
				}
				if (fields.get("niveau") != null) {
					trottoir.setNiveau(fields.get("niveau").getAsString());
				}
				Gson gson = new Gson();
				GeoShape shape = null;
				try {
					shape = gson.fromJson(fields.get("geom"),
							GeoShapeLineString.class);
				} catch (JsonSyntaxException e) {
					try {
						shape = gson.fromJson(fields.get("geom"),
								GeoShapeMultiLineString.class);
					} catch (JsonSyntaxException ex) {
						fail++;
					}
				}
				if (shape != null) {
					trottoir.setShape(shape);
					list.add(trottoir);
				}
			}

		}

		try {
			trottoirRepository.save(list);
			System.out.println("imported: " + asJsonArray.size() + " with "
					+ fail + " failures");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	private int grabPart(UrlBuilder builder) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(builder.build());

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
						importRecords(records);
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

	public void grabData(Double latitude, Double longitude, int distance_meter) {

		UrlBuilder builder = new UrlBuilder();
		builder.withRows(OpenDataParisTrottoirAPI.BULK_PACK)
				.withGeodistanceFilter(latitude, longitude, distance_meter);

		int total = grabPart(builder);
		int current = OpenDataParisTrottoirAPI.BULK_PACK;

		while (current < total) {
			builder = new UrlBuilder();
			builder.withRows(OpenDataParisTrottoirAPI.BULK_PACK);
			current += OpenDataParisTrottoirAPI.BULK_PACK;
			builder.withStart(current);
			grabPart(builder);
		}

	}

}
