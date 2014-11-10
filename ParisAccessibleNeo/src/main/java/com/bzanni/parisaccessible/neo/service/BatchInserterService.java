package com.bzanni.parisaccessible.neo.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
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

	@Resource
	private MemcachedService cache;

	private BatchInserter inserter;
	private BatchInserterIndexProvider indexProvider;

	public void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public void init() {
		if (inserter == null) {
			Map<String, String> config = new HashMap<>();
			config.put("neostore.nodestore.db.mapped_memory", "25M");
			config.put("neostore.relationshipstore.db.mapped_memory", "50M");
			config.put("neostore.propertystore.db.mapped_memory", "90M");
			config.put("neostore.propertystore.db.strings.mapped_memory",
					"130M");
			config.put("neostore.propertystore.db.arrays.mapped_memory", "130M");
			// neostore.nodestore.db.mapped_memory=25M
			// neostore.relationshipstore.db.mapped_memory=50M
			// neostore.propertystore.db.mapped_memory=90M
			// neostore.propertystore.db.strings.mapped_memory=130M
			// neostore.propertystore.db.arrays.mapped_memory=130M

			File file = new File(neoDataPath);
			if (file.exists() && file.isDirectory()) {
				deleteFolder(file);

			}
			inserter = BatchInserters.inserter(neoDataPath,
					new DefaultFileSystemAbstraction(), config);

			// inserter.createDeferredSchemaIndex(TrottoirIndexerService.locationLabel)
			// .on("id").create();

			indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		}
	}

	@PreDestroy
	public void destroy() {
		this.flushAndShutdown();
	}

	public Long addLocationToInserter(Location location) {

		long createNode = inserter.createNode(location.getMap(),
				DynamicLabel.label(location.getLabel()));
		System.out.println("Create " + location.getLabel() + ": " + createNode);
		location.setGraphId(createNode);

		cache.set(location.getId(), location);
		return createNode;
	}

	public void addBidirectionalToInserter(List<? extends Path> list) {
		for (Path p : list) {
			addBidirectionalToInserter(p);
		}
	}

	public void addBidirectionalToInserter(Path path) {
		Long start = path.getStart().getGraphId();
		Long end = path.getEnd().getGraphId();
		if (start == null) {
			String id = path.getStart().getId();
			Location s = (Location) cache.get(id);
			if (s != null && s.getGraphId() != null) {
				start = s.getGraphId();
			} else {
				start = this.addLocationToInserter(path.getStart());
			}
		}
		if (end == null) {
			String id = path.getEnd().getId();
			Location s = (Location) cache.get(id);
			if (s != null && s.getGraphId() != null) {
				end = s.getGraphId();
			} else {
				end = this.addLocationToInserter(path.getEnd());
			}
		}

		if (start != null && end != null) {
			inserter.createRelationship(start, end,
					DynamicRelationshipType.withName(path.getType()),
					path.getMap());

			inserter.createRelationship(end, start,
					DynamicRelationshipType.withName(path.getType()),
					path.getMap());

			System.out.println("Create REL: " + path.getType());
		}

	}

	public void flushAndShutdown() {
		indexProvider.shutdown();
		inserter.shutdown();
	}

}
