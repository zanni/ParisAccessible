package com.bzanni.parisaccessible.indexer.controller;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
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
import com.bzanni.parisaccessible.indexer.service.TrottoirIndexerService;

@Configuration
@Configurable
@ComponentScan
@EnableAutoConfiguration
@ImportResource({ "classpath:/META-INF/spring/servlet-context.xml" })
public class Application {

	@Value("${rabbitmq_host}")
	private String rabbitmqHost;

	@Value("${rabbitmq_port}")
	private Integer rabbitmqPort;

	final static String workflowQueueName = "workflow";
	final static String locationQueueName = "location";
	final static String pathQueueName = "path";

	@Bean
	public MessageConverter messageConverter(){
		JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();
		return jsonMessageConverter;
	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
				rabbitmqHost, rabbitmqPort);
		return connectionFactory;
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
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
	}

	@Bean
	SimpleMessageListenerContainer containerworkflowQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.workflowQueueName);
		WorkflowListener listener = new WorkflowListener();
		container.setMessageListener(listener);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer containerlocationQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.locationQueueName);
		LocationListener listener = new LocationListener();
		container.setMessageListener(listener);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer containerpathQueueName(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(Application.pathQueueName);
		PathListener listener = new PathListener();
		container.setMessageListener(listener);
		return container;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(
				Application.class, args);

		CommandLineParser parser = new BasicParser();

		// create the Options
		Options options = new Options();
		options.addOption("index_trottoir", "index_trottoir", false,
				"do not hide entries starting with .");

		options.addOption("index_trip", "index_trip", false,
				"do not hide entries starting with .");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("index_trottoir")) {
				final TrottoirIndexerService trottoirIndexer = run
						.getBean(TrottoirIndexerService.class);

				trottoirIndexer.indexTrottoir();
			} else if (line.hasOption("index_trip")) {

			}

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		run.close();
		System.exit(0);
	}
}
