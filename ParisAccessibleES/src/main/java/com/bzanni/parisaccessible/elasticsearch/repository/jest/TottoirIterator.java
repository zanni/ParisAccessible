package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.TrottoirRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class TottoirIterator implements Iterator<List<Trottoir>> {

	private final static int DEFAULT_BULK = 1000;

	private TrottoirRepository repository;

	private Integer bulk;

	private int cursor = 0;

	private List<Trottoir> sourceAsObjectList;

	private JestQueryEngine engine;

	private int index_worker = 0;

	private int total_worker = 1;

	public TottoirIterator(TrottoirRepository repository, int index_worker,
			int total_worker) {
		this.repository = repository;
		this.bulk = TottoirIterator.DEFAULT_BULK;
		engine = new JestQueryEngine();
		this.index_worker = index_worker;
		this.total_worker = total_worker;
		this.cursor = this.index_worker * this.bulk;
	}

	public TottoirIterator(TrottoirRepository repository) {
		this.repository = repository;
		this.bulk = TottoirIterator.DEFAULT_BULK;
		engine = new JestQueryEngine();
		this.index_worker = 0;
		this.total_worker = 1;
		this.cursor = this.index_worker * this.bulk;

	}

	public TottoirIterator(TrottoirRepository repository, int index_worker,
			int total_worker, int bulk) {
		this.repository = repository;
		this.bulk = bulk;
		engine = new JestQueryEngine();
		this.index_worker = index_worker;
		this.total_worker = total_worker;
		this.cursor = this.index_worker * this.bulk;
	}

	private static List<Trottoir> parse(JsonElement records) {
		JsonArray asJsonArray = records.getAsJsonArray();
		List<Trottoir> list = new ArrayList<Trottoir>();
		for (int i = 0; i < asJsonArray.size(); i++) {
			Trottoir trottoir = new Trottoir();
			JsonElement jsonElement = asJsonArray.get(i);
			JsonObject record = jsonElement.getAsJsonObject();

			String id = record.get("_id").getAsString();
			JsonObject fields = record.get("_source").getAsJsonObject();

			if (id != null && fields != null) {

				trottoir.setId(id);
				if (fields.get("info") != null) {
					trottoir.setInfo(fields.get("info").getAsString());
				}
				Gson gson = new Gson();
				GeoShape shape = null;
				try {
					shape = gson.fromJson(fields.get("shape"),
							GeoShapeLineString.class);
				} catch (JsonSyntaxException e) {
					try {
						shape = gson.fromJson(fields.get("shape"),
								GeoShapeMultiLineString.class);
					} catch (JsonSyntaxException ex) {
						ex.printStackTrace();
					}
				}
				if (shape != null) {
					trottoir.setShape(shape);
					list.add(trottoir);
				}
			}

		}

		return list;
	}

	public static List<Trottoir> parseResult(JsonObject result) {
		if (result != null) {
			// JsonElement nhits = result.get("nhits");
			JsonElement records = result.get("hits");
			if (records != null) {
				return parse(records.getAsJsonObject().get("hits"));
			}

		}

		return null;
	}

	@Override
	public boolean hasNext() {

		System.out
				.println("query: from: " + cursor + " to: " + (cursor + bulk));

		int marge = this.total_worker * bulk;

		String query = engine.matchAllQuery();

		Search build = new Search.Builder(query).setParameter("size", bulk)
				.setParameter("from", cursor).addIndex(repository.getIndex())
				.addType(repository.getType()).build();
		try {
			SearchResult execute = repository.getClient().execute(build);

			Integer total = execute.getTotal();

			if (execute.isSucceeded() && total != null) {

				sourceAsObjectList = parseResult(execute.getJsonObject());

				// JsonElement fromJson = gson.fromJson(rd, JsonElement.class);
				// sourceAsObjectList = execute.getSourceAsObjectList(klass);
				cursor += marge;
				if (cursor > total) {
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Trottoir> next() {
		return sourceAsObjectList;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
