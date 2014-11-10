package com.bzanni.parisaccessible.indexer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.Path;

@Service
public class LocationPublisher {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Resource
	private MessageConverter messageConverter;

	public void startWorker(String worker) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("phase", "start");
		map.put("worker", worker);
		rabbitTemplate.convertAndSend("workflow",map);
	}

	public void endWorker(String worker) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("phase", "end");
		map.put("worker", worker);
		rabbitTemplate.convertAndSend("workflow",map);
	}

	public void addBidirectionalToInserter(Path path) {
		rabbitTemplate.convertAndSend("path", path);
	}

	public void addBidirectionalToInserter(List<? extends Path> path) {
		for (Path p : path) {
			this.addBidirectionalToInserter(p);
		}
	}

	public void addLocationToInserter(Location location) {
		rabbitTemplate.convertAndSend("location", location);
	}
}
