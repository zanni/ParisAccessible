package com.bzanni.parisaccessible.indexer.controller;

import javax.annotation.Resource;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.bzanni.parisaccessible.indexer.listener.PathListener;
import com.bzanni.parisaccessible.indexer.listener.WorkflowListener;
import com.bzanni.parisaccessible.indexer.service.IndexWorkerSyncService;

@Configuration
@Configurable
@ComponentScan
@EnableAutoConfiguration
@ImportResource({ "classpath:/META-INF/spring/servlet-context.xml" })
public class Application {

	@Resource
	private IndexWorkerSyncService syncService;

	@Resource
	private RabbitConf conf;

	final static String workflowQueueName = "workflow";
	final static String locationQueueName = "location";
	final static String pathQueueName = "path";

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
				conf.getRabbitmqHost(), conf.getRabbitmqPort());
		connectionFactory.setUsername(conf.getRabbitmqUsername());
		connectionFactory.setPassword(conf.getRabbitmqPassword());
		return connectionFactory;
	}

	
	@Bean
	Queue workflowQueue() {
		return new Queue(Application.workflowQueueName, false);
	}

	@Bean
	Queue pathQueue() {
		return new Queue(Application.pathQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("parisaccessible");
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setExchange("parisaccessible");
		// template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	
	@Bean
	Binding workflowQueueBinding(Queue workflowQueue, TopicExchange exchange) {
		return BindingBuilder.bind(workflowQueue).to(exchange)
				.with(workflowQueue.getName());
	}


	@Bean
	Binding pathQueueBinding(Queue pathQueue, TopicExchange exchange) {
		return BindingBuilder.bind(pathQueue).to(exchange)
				.with(pathQueue.getName()+".*");
	}

	@Bean
	MessageListenerAdapter pathQueueAdapter() {

		return new MessageListenerAdapter(new PathListener(syncService));
	}

	@Bean
	MessageListenerAdapter workflowQueueAdapter() {
		return new MessageListenerAdapter(new WorkflowListener(syncService));
	}

	@Bean
	SimpleMessageListenerContainer containerworkflowQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter workflowQueueAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.workflowQueueName);
		container.setMessageListener(workflowQueueAdapter);
		container.setAcknowledgeMode(AcknowledgeMode.NONE);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer containerpathQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter pathQueueAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.pathQueueName);
		container.setMessageListener(pathQueueAdapter);
		container.setAcknowledgeMode(AcknowledgeMode.NONE);
		return container;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(
				Application.class, args);

		// run.close();
		// System.exit(0);
	}
}
