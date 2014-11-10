package com.bzanni.parisaccessible.indexer.controller;

import javax.annotation.Resource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.bzanni.parisaccessible.indexer.listener.LocationListener;
import com.bzanni.parisaccessible.indexer.listener.PathListener;
import com.bzanni.parisaccessible.indexer.listener.WorkflowListener;
import com.bzanni.parisaccessible.neo.service.BatchInserterService;

@Configuration
@Configurable
@ComponentScan
@EnableAutoConfiguration
@ImportResource({ "classpath:/META-INF/spring/servlet-context.xml" })
public class Application {

	@Resource
	private RabbitConf conf;

	@Resource
	private BatchInserterService batchService;

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
	public MessageConverter jsonMessageConverter() {
		Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
		return jsonMessageConverter;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());

		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	@Bean
	Queue workflowQueue() {
		return new Queue(Application.workflowQueueName, false);
	}

	@Bean
	Queue locationQueue() {
		return new Queue(Application.locationQueueName, false);
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
	Binding workflowQueueBinding(Queue workflowQueue, TopicExchange exchange) {
		return BindingBuilder.bind(workflowQueue).to(exchange)
				.with(workflowQueue.getName());
	}

	@Bean
	Binding locationQueueBinding(Queue locationQueue, TopicExchange exchange) {
		return BindingBuilder.bind(locationQueue).to(exchange)
				.with(locationQueue.getName());
	}

	@Bean
	Binding pathQueueBinding(Queue pathQueue, TopicExchange exchange) {
		return BindingBuilder.bind(pathQueue).to(exchange)
				.with(pathQueue.getName());
	}

	@Bean
	MessageListenerAdapter pathQueueAdapter() {

		return new MessageListenerAdapter(new PathListener(batchService));
	}

	@Bean
	MessageListenerAdapter workflowQueueAdapter() {
		return new MessageListenerAdapter(new WorkflowListener(batchService));
	}

	@Bean
	MessageListenerAdapter locationQueueAdapter() {
		return new MessageListenerAdapter(new LocationListener(batchService));
	}

	@Bean
	SimpleMessageListenerContainer containerworkflowQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter workflowQueueAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.workflowQueueName);
		container.setMessageListener(workflowQueueAdapter);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer containerlocationQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter locationQueueAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.locationQueueName);
		container.setMessageListener(locationQueueAdapter);
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
		return container;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(
				Application.class, args);

		// run.close();
		// System.exit(0);
	}
}
