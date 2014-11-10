package com.bzanni.parisaccessible.indexer.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.neo.business.Path;
import com.bzanni.parisaccessible.neo.service.BatchInserterService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PathListener implements MessageListener {

	private BatchInserterService batchInserter;

	private ObjectMapper mapper;

	public PathListener(BatchInserterService batchInserter) {
		this.batchInserter = batchInserter;
		mapper = new ObjectMapper();
		this.batchInserter.init();
	}

	@Override
	public void onMessage(Message message) {

		try {

			byte[] body = message.getBody();
			Path readValue = mapper.readValue(new String(body), Path.class);

			batchInserter.addBidirectionalToInserter(readValue);
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
