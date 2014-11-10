package com.bzanni.parisaccessible.indexer.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.Path;

@Service
public class LocationPublisher {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void startWorker(String worker) {
		rabbitTemplate.convertAndSend("workflow", "start:" + worker);
	}

	public void endWorker(String worker) {
		rabbitTemplate.convertAndSend("workflow", "end:" + worker);
	}

	public void addBidirectionalToInserter(Path path) {
		this.addBidirectionalToInserter(Arrays.asList(path));
	}

	public void addBidirectionalToInserter(List<? extends Path> path) {
		rabbitTemplate.convertAndSend("path", path);
	}

	public void addLocationToInserter(Location location) {
		rabbitTemplate.convertAndSend("location", location);
	}
}
