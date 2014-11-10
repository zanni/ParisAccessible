package com.bzanni.parisaccessible.indexer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.neo.business.Location;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationListener implements MessageListener {

	ObjectMapper mapper;

	public LocationListener() {
		mapper = new ObjectMapper();
	}

	@Override
	public void onMessage(Message message) {

		byte[] body = message.getBody();
		String string = new String(body);
		Location convertValue = mapper.convertValue(string, Location.class);

	}

}
