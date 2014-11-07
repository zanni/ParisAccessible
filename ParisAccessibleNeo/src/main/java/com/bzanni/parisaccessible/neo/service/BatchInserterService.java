package com.bzanni.parisaccessible.neo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.kernel.DefaultFileSystemAbstraction;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.Path;

@Service
@Configurable
public class BatchInserterService {

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	private BatchInserter inserter;
	private BatchInserterIndexProvider indexProvider;

	@PostConstruct
	public void init() {
		Map<String, String> config = new HashMap<>();
		config.put("neostore.nodestore.db.mapped_memory", "25M");
		config.put("neostore.relationshipstore.db.mapped_memory", "50M");
		config.put("neostore.propertystore.db.mapped_memory", "90M");
		config.put("neostore.propertystore.db.strings.mapped_memory", "130M");
		config.put("neostore.propertystore.db.arrays.mapped_memory", "130M");
		// neostore.nodestore.db.mapped_memory=25M
		// neostore.relationshipstore.db.mapped_memory=50M
		// neostore.propertystore.db.mapped_memory=90M
		// neostore.propertystore.db.strings.mapped_memory=130M
		// neostore.propertystore.db.arrays.mapped_memory=130M
		inserter = BatchInserters.inserter(neoDataPath,
				new DefaultFileSystemAbstraction(), config);

		// inserter.createDeferredSchemaIndex(TrottoirIndexerService.locationLabel)
		// .on("id").create();

		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
	}

	@PreDestroy
	public void destroy(){
		this.flushAndShutdown();
	}
	
	public void addLocationToInserter(Location location) {
		long createNode = inserter.createNode(location.getMap(),
				location.getLabel());
		System.out.println("Create " + location.getLabel().name() + ": "
				+ createNode);
		location.setGraphId(createNode);
	}

	public void addBidirectionalToInserter(List<? extends Path> list) {
		for (Path p : list) {
			addBidirectionalToInserter(p);
		}
	}

	public void addBidirectionalToInserter(Path path) {
		inserter.createRelationship(path.getStart().getGraphId(), path.getEnd()
				.getGraphId(), path.getRelationshipType(), path.getMap());

		inserter.createRelationship(path.getEnd().getGraphId(), path.getStart()
				.getGraphId(), path.getRelationshipType(), path.getMap());

		System.out.println("Create REL: " + path.getRelationshipType().name());
	}

	public void flushAndShutdown() {
		indexProvider.shutdown();
		inserter.shutdown();
	}

}
