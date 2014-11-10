package com.bzanni.parisaccessible.indexer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;


public class WorkflowListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		byte[] messageContent = message.getBody();
			        System.out.println("Message receied is " + messageContent);
		
	}

}
