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

import com.bzanni.parisaccessible.elasticsearch.repository.jest.ParisAccessibleJestClient;
import com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.AccessibilityImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs.GtfsImport;
import com.bzanni.parisaccessible.elasticsearch.service.csv.gtfs.GtfsTripCsvImportThread;
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
		options.addOption("gtfs_route", "gtfs_route", false,
				"do not hide entries starting with .");

		options.addOption("gtfs_stop", "gtfs_stop", false,
				"do not hide entries starting with .");

		options.addOption("gtfs_other", "gtfs_other", false,
				"do not hide entries starting with .");

		options.addOption("access_equipement", "access_equipement", false,
				"do not hide entries starting with .");
		
		options.addOption("access_trottoir", "access_trottoir", true,
				"do not hide entries starting with .");
		
		options.addOption("access_passagepieton", "access_passagepieton", true,
				"do not hide entries starting with .");

		options.addOption("gtfs_trip", "gtfs_trip", true,
				"do not hide entries starting with .");

		options.addOption("index", "index", true,
				"do not hide entries starting with .");

		final GtfsImport ratpGtfs = run.getBean(GtfsImport.class);

		final AccessibilityImport access = run
				.getBean(AccessibilityImport.class);

		ParisAccessibleConfigurationBean conf = run
				.getBean(ParisAccessibleConfigurationBean.class);

		ParisAccessibleJestClient client = run
				.getBean(ParisAccessibleJestClient.class);

		try {
			client.indexing(false);
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("gtfs_route")) {
				ratpGtfs.importRoute(500);
				access.importRoute(500);

			}
			else if (line.hasOption("gtfs_stop")) {
				ratpGtfs.importStop(2000);
				ratpGtfs.importStopTransfert(2000);
				access.importStop(2000);

			}
			else if (line.hasOption("gtfs_other")) {
				ratpGtfs.importAgency(500);
				ratpGtfs.importService(500);
				ratpGtfs.importServiceCalendar(500);

			}
			else if (line.hasOption("access_equipement")) {
				access.importEquipement(2000);

			} 
			else if (line.hasOption("access_trottoir")) {
				
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("access_trottoir"));
				File folder = new File(conf.getAccessibilityPath());
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							access.importTrottoir(name, 100);
						}
					}
				}
			}
			else if (line.hasOption("access_passagepieton")) {
				
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("access_passagepieton"));
				File folder = new File(conf.getAccessibilityPath());
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							access.importPassagePieton(name, 100);

						}
					}
				}

			}
			else if (line.hasOption("gtfs_trip")) {

				Pattern compile = Pattern.compile(line
						.getOptionValue("gtfs_trip"));
				File folder = new File(conf.getGtfsPath());
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							GtfsTripCsvImportThread thread = run
									.getBean(GtfsTripCsvImportThread.class);
							thread.setBulk(2000);
							thread.setCsvFile(name);
							thread.run();

						}
					}
				}

			}

			client.indexing(true);

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
