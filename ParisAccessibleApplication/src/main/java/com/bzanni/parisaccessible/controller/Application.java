package com.bzanni.parisaccessible.controller;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.bzanni.parisaccessible.elasticsearch.service.opendata.ratp.RatpGtfsTripCsvImportThread;
import com.bzanni.parisaccessible.elasticsearch.service.util.ParisAccessibleConfigurationBean;

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
		options.addOption("b", "base", false,
				"do not hide entries starting with .");
		options.addOption("t", "trip", true,
				"do not hide entries starting with .");
		options.addOption("s", "stoptime", true,
				"do not hide entries starting with .");

		RatpGtfsImport ratpGtfs = run.getBean(RatpGtfsImport.class);

		ParisAccessibleConfigurationBean conf = run
				.getBean(ParisAccessibleConfigurationBean.class);

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("base")) {

				ratpGtfs.importRoute(500);
				ratpGtfs.importAgency(500);
				ratpGtfs.importService(500);
				ratpGtfs.importServiceCalendar(500);
				ratpGtfs.importStop(2000);
				ratpGtfs.importStopTransfert(2000);

			} else if (line.hasOption("trip")) {


				Pattern compile = Pattern.compile(line.getOptionValue("trip"));
				File folder = new File(conf.getGtfsPath());
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							RatpGtfsTripCsvImportThread thread = run
									.getBean(RatpGtfsTripCsvImportThread.class);
							thread.setBulk(2000);
							thread.setCsvFile(name);
							thread.run();

						}
					}
				}

			}

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

		run.close();
		System.exit(0);
	}

}
