package com.bzanni.parisaccessible.indexer.listener;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.neo.service.BatchInserterService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowListener implements MessageListener {

	private BatchInserterService batchInserter;

	private ObjectMapper mapper;

	public WorkflowListener(BatchInserterService batchInserter) {
		this.batchInserter = batchInserter;
		mapper = new ObjectMapper();
		this.batchInserter.init();
	}

	@Override
	public void onMessage(Message message) {

		try {
			byte[] body = message.getBody();
			Map<String, String> readValue = mapper.readValue(new String(body),
					Map.class);

			System.out.println(readValue);
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
