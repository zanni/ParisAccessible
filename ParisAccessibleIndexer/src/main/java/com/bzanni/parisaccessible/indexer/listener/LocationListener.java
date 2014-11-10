package com.bzanni.parisaccessible.indexer.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.service.BatchInserterService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationListener implements MessageListener {

	private BatchInserterService batchInserter;

	private ObjectMapper mapper;

	public LocationListener(BatchInserterService batchInserter) {
		this.batchInserter = batchInserter;
		mapper = new ObjectMapper();
		this.batchInserter.init();
	}

	@Override
	public void onMessage(Message message) {

		try {
			byte[] body = message.getBody();
			Location readValue = mapper.readValue(new String(body),
					Location.class);

			batchInserter.addLocationToInserter(readValue);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
