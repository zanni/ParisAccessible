package com.bzanni.parisaccessible.indexer.listener;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.bzanni.parisaccessible.indexer.service.IndexWorkerSyncService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class WorkflowListener implements MessageListener {

	private IndexWorkerSyncService syncService;

	public WorkflowListener(IndexWorkerSyncService syncService) {
		this.syncService = syncService;
	}

	@Override
	public void onMessage(Message message) {

		try {
			
			syncService.receiveWorflow(message);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
