package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class JestRequestIterator<S extends JestBusiness> implements
		Iterator<List<S>> {

	private final static int DEFAULT_BULK = 20;

	private AbstractJestRepository<S> repository;

	private Integer bulk;

	private int cursor = 0;

	private Class<S> klass;

	private List<S> sourceAsObjectList;

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass) {
		this.repository = repository;
		this.bulk = JestRequestIterator.DEFAULT_BULK;
		this.klass = klass;

	}

	public JestRequestIterator(AbstractJestRepository<S> repository,
			Class<S> klass, int bulk) {
		this.repository = repository;
		this.bulk = bulk;
		this.klass = klass;
	}

	@Override
	public boolean hasNext() {

		String query = "{ \"query\":"
				+ QueryBuilders.matchAllQuery().buildAsBytes().toUtf8() + "}";

		Search build = new Search.Builder(query).setParameter("size", bulk)
				.setParameter("from", cursor).addIndex(repository.getIndex())
				.addType(repository.getType()).build();
		try {
			SearchResult execute = repository.getClient().execute(build);
			
			Integer total = execute.getTotal();
			
			if (execute.isSucceeded() && total != null) {
				sourceAsObjectList = execute.getSourceAsObjectList(klass);
				cursor += bulk;
				if(cursor > total){
					return false;
				}
				else {
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
