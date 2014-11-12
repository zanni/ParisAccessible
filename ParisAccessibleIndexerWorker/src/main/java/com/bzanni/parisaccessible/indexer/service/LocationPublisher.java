package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Path;

@Service
public class LocationPublisher {

	private final static int BULK = 1000;

	private List<Object> pathsBulk = new ArrayList<Object>();
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void startWorker(int index_worker, int total_worker) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cycle", "start");
		map.put("index_worker", index_worker);
		map.put("phase", "index");
		map.put("total_worker", total_worker);
		rabbitTemplate.convertAndSend("workflow", map);
	}

	public void endWorker(int index_worker, int total_worker) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cycle", "end");
		map.put("index_worker", index_worker);
		map.put("phase", "index");
		map.put("total_worker", total_worker);
		rabbitTemplate.convertAndSend("workflow", map);
	}

	public void addBidirectionalToInserter(int index_worker, int total_worker,
			Path path) {
		if (path != null) {
			pathsBulk.add(path);
			if (pathsBulk.size() >= LocationPublisher.BULK) {
				rabbitTemplate.setExchange("parisaccessible");
				rabbitTemplate
						.convertAndSend("path." + index_worker, pathsBulk);
				pathsBulk = new ArrayList<Object>();
			}
		}
	}

	public void addBidirectionalToInserter(int index_worker, int total_worker,
			List<? extends Path> path) {
		if (!path.isEmpty()) {
			pathsBulk.addAll(path);
			if (pathsBulk.size() >= LocationPublisher.BULK) {
				rabbitTemplate.setExchange("parisaccessible");
				rabbitTemplate
						.convertAndSend("path." + index_worker, pathsBulk);
				pathsBulk = new ArrayList<Object>();
			}
		}
	}

	public void emptyBulk(int index_worker, int total_worker) {
		rabbitTemplate.setExchange("parisaccessible");
		rabbitTemplate.convertAndSend("path." + index_worker, pathsBulk);
	}
}
