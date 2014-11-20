package com.bzanni.parisaccessible.app.controller;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Configurable
@ComponentScan
@EnableAutoConfiguration
@ImportResource({ "classpath:/META-INF/spring/servlet-context.xml" })
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(
				Application.class, args);
		
		

//		run.close();
//		System.exit(0);
	}
}
