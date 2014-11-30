package com.bzanni.parisaccessible.neo.service;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.tooling.GlobalGraphOperations;
import org.springframework.stereotype.Service;

@Service
public class SpatialIndexerService {

	public final static String SIDWAY_LAYER_NAME = "sidway";
	public final static String PIETON_LAYER_NAME = "pieton";
	public final static String STOP_LAYER_NAME = "stop";

	private SimplePointLayer getOrCreateLayer(
			SpatialDatabaseService spatialService, String name) {
		if (spatialService.containsLayer(name)) {
			return (SimplePointLayer) spatialService.getLayer(name);
		} else {
			return spatialService.createSimplePointLayer(name, "lat", "lon");
		}
	}

	public void index(String folder) {

		GraphDatabaseService database = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(folder)
				.setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size,
						"500M")
				.setConfig(
						GraphDatabaseSettings.relationshipstore_mapped_memory_size,
						"2G")
				.setConfig(
						GraphDatabaseSettings.relationshipstore_mapped_memory_size,
						"2G")
				.setConfig(GraphDatabaseSettings.string_block_size, "60")
				.setConfig(GraphDatabaseSettings.array_block_size, "300")
				.newGraphDatabase();

		SpatialDatabaseService spatialService = new SpatialDatabaseService(
				database);

		Transaction tx = database.beginTx();

		SimplePointLayer sidwayLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.SIDWAY_LAYER_NAME);
		SimplePointLayer pietonLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.PIETON_LAYER_NAME);
		SimplePointLayer stopLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.STOP_LAYER_NAME);

		tx.success();
		tx.close();

		tx = database.beginTx();

		Iterable<Node> allNodes = GlobalGraphOperations.at(database)
				.getAllNodes();
		List<Node> buffer = new ArrayList<Node>();
		int i = 0;
		int j = 0;
		for (Node n : allNodes) {

			buffer.add(n);
			i++;
			if (i % ShortestPathService.BULK_INDEX == 0) {

				database.beginTx();
				for (Node node : buffer) {
					if (node.hasProperty("lat") && node.hasProperty("lon")) {
						for (Label label : node.getLabels()) {
							if (label.name().equals("SIDWAY")) {
								sidwayLayer.add(node);
							} else if (label.name().equals("PIETON")) {
								pietonLayer.add(node);
							} else if (label.name().equals("STOP")) {
								stopLayer.add(node);
							}
						}

						j++;
					}
				}
				System.out.println("Indexed: " + j + " over " + i);
				buffer = new ArrayList<Node>();
				tx.success();
				tx.close();
				tx = database.beginTx();

			}

		}
		database.beginTx();
		for (Node node : buffer) {
			if (node.hasProperty("lat") && node.hasProperty("lon")) {
				for (Label label : node.getLabels()) {
					if (label.name().equals("SIDWAY")) {
						sidwayLayer.add(node);
					} else if (label.name().equals("PIETON")) {
						pietonLayer.add(node);
					} else if (label.name().equals("STOP")) {
						stopLayer.add(node);
					}
				}
				j++;
			}
		}
		System.out.println("Indexed: " + i);
		buffer = new ArrayList<Node>();
		tx.success();
		tx.close();

		database.shutdown();
	}
}
