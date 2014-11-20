package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.Iterator;
import java.util.List;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class JestRequestIterator<S extends JestBusiness> implements
		Iterator<List<S>> {

	private final static int DEFAULT_BULK = 20;

	private AbstractJestRepository<S> repository;

	private Integer bulk;

	private int cursor = 0;

	private Class<S> klass;

	private List<S> sourceAsObjectList;

	private JestQueryEngine engine;

	private int index_worker = 0;

	private int total_worker = 1;

	private String query;

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass, int index_worker, int total_worker) {
		this(repository, klass, index_worker, total_worker, null,
				JestRequestIterator.DEFAULT_BULK);

	}

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass, int index_worker, int total_worker, String query) {
		this(repository, klass, index_worker, total_worker, query,
				JestRequestIterator.DEFAULT_BULK);

	}

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass) {
		this(repository, klass, 0, 1, null, JestRequestIterator.DEFAULT_BULK);

	}
	
	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass, String query) {
		this(repository, klass, 0, 1, query, JestRequestIterator.DEFAULT_BULK);

	}

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass, int index_worker, int total_worker, String query,
			int bulk) {
		this.bulk = bulk;
		this.repository = repository;
		this.bulk = bulk;
		this.klass = klass;
		engine = new JestQueryEngine();
		this.index_worker = index_worker;
		this.total_worker = total_worker;
		this.cursor = this.index_worker * this.bulk;
		if (query == null) {
			query = engine.matchAllQuery();
		}
	}

	@Override
	public boolean hasNext() {

		System.out
				.println("query: from: " + cursor + " to: " + (cursor + bulk));

		int marge = this.total_worker * bulk;

		Search build = new Search.Builder(query).setParameter("size", bulk)
				.setParameter("from", cursor).addIndex(repository.getIndex())
				.addType(repository.getType()).build();
		try {
			SearchResult execute = repository.getClient().execute(build);

			Integer total = execute.getTotal();

			if (execute.isSucceeded() && total != null) {
				sourceAsObjectList = execute.getSourceAsObjectList(klass);
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
	public List<S> next() {
		return sourceAsObjectList;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
