package com.bzanni.parisaccessible.indexer.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.indexer.service.IndexWorkerSyncService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PathListener implements MessageListener {

	private IndexWorkerSyncService syncService;

	public PathListener(IndexWorkerSyncService syncService) {
		this.syncService = syncService;

	}

	@Override
	public void onMessage(Message message) {
		try {
			
			syncService.receivePath(message);

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
