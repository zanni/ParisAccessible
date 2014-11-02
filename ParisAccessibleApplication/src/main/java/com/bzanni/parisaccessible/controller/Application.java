package com.bzanni.parisaccessible.controller;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp.RatpGtfsImport;

@Configuration
@Configurable
@ComponentScan
@EnableAutoConfiguration
@ImportResource({ "classpath:/META-INF/spring/servlet-context.xml" })
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(
				Application.class, args);

		CommandLineParser parser = new BasicParser();

		// create the Options
		Options options = new Options();
		options.addOption("r", "route", true,
				"do not hide entries starting with .");
		options.addOption("ra", "route_accessibility", true,
				"do not hide entries starting with .");
		options.addOption("s", "stop", true,
				"do not hide entries starting with .");
		options.addOption("sa", "stop_accessibility", true,
				"do not hide entries starting with .");
		options.addOption("c", "service", true,
				"do not hide entries starting with .");
		options.addOption("t", "trip", true,
				"do not hide entries starting with .");

		RatpGtfsImport ratpGtfs = run.getBean(RatpGtfsImport.class);

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("route")) {

				ratpGtfs.importRoute(line.getOptionValue("route"), 500);

			} if (line.hasOption("route_accessibility")) {

				ratpGtfs.importRouteAccessibility(
						line.getOptionValue("route_accessibility"), 500);

			}
			else if (line.hasOption("stop")) {

				ratpGtfs.importStop(line.getOptionValue("stop"), 2000);

			}
			else if (line.hasOption("stop_accessibility")) {

				ratpGtfs.importStopAccessibility(
						line.getOptionValue("stop_accessibility"), 2000);

			}
			else if (line.hasOption("service")) {

				ratpGtfs.importService(line.getOptionValue("service"), 500);

			} else if (line.hasOption("trip")) {

				ratpGtfs.importTrip(line.getOptionValue("trip"), 5000);

			}

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

		run.close();
		System.exit(0);
	}

}
