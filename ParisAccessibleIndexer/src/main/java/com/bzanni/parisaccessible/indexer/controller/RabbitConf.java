package com.bzanni.parisaccessible.indexer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitConf {
	
	@Value("${rabbitmq_host}")
	private String rabbitmqHost;

	@Value("${rabbitmq_port}")
	private Integer rabbitmqPort;

	@Value("${rabbitmq_username}")
	private String rabbitmqUsername;

	@Value("${rabbitmq_password}")
	private String rabbitmqPassword;

	public String getRabbitmqHost() {
		return rabbitmqHost;
	}

	public void setRabbitmqHost(String rabbitmqHost) {
		this.rabbitmqHost = rabbitmqHost;
	}

	public Integer getRabbitmqPort() {
		return rabbitmqPort;
	}

	public void setRabbitmqPort(Integer rabbitmqPort) {
		this.rabbitmqPort = rabbitmqPort;
	}

	public String getRabbitmqUsername() {
		return rabbitmqUsername;
	}

	public void setRabbitmqUsername(String rabbitmqUsername) {
		this.rabbitmqUsername = rabbitmqUsername;
	}

	public String getRabbitmqPassword() {
		return rabbitmqPassword;
	}

	public void setRabbitmqPassword(String rabbitmqPassword) {
		this.rabbitmqPassword = rabbitmqPassword;
	}

}
