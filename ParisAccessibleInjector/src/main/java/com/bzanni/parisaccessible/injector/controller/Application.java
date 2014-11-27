package com.bzanni.parisaccessible.injector.controller;

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
import com.bzanni.parisaccessible.injector.service.csv.accessibility.AccessibilityImport;
import com.bzanni.parisaccessible.injector.service.csv.gtfs.GtfsImport;
import com.bzanni.parisaccessible.injector.service.csv.gtfs.GtfsTripCsvImportThread;
import com.bzanni.parisaccessible.injector.service.util.ParisAccessibleConfigurationBean;

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
		options.addOption("index_worker", "index_worker", true,
				"do not hide entries starting with .");

		options.addOption("total_worker", "total_worker", true,
				"do not hide entries starting with .");
		
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

		options.addOption("gtfs_stoptime", "gtfs_stoptime", true,
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
				
			}
			if (line.hasOption("gtfs_stop")) {
				ratpGtfs.importStop(2000);
				ratpGtfs.importService(500);
			}
			if (line.hasOption("gtfs_other")) {
				ratpGtfs.importAgency(500);
				access.importRoute(500);
				//ratpGtfs.importServiceCalendar(500);
				//ratpGtfs.importStopTransfert(2000);
				access.importStop(2000);

			}
			if (line.hasOption("access_equipement")) {
				access.importEquipement(2000);
			}
			if (line.hasOption("access_trottoir")) {
				
				String index_worker = line.getOptionValue("index_worker");
				String total_worker = line.getOptionValue("total_worker");

				Integer index_worker_int = Integer.valueOf(index_worker);
				Integer total_worker_int = Integer.valueOf(total_worker);
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("access_trottoir"));
				
				
				File folder = new File(conf.getAccessibilityPath());
				
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							
							String[] split = name.split("\\.");
							if(split.length == 3){
								Integer index = Integer.valueOf(split[split.length - 1]);
								
								if(index!=null && index.equals(index_worker_int)){
									access.importTrottoir(name, 100);
								}
							}
							
							
						}
					}
				}
			}
			if (line.hasOption("access_passagepieton")) {

				String index_worker = line.getOptionValue("index_worker");
				String total_worker = line.getOptionValue("total_worker");

				Integer index_worker_int = Integer.valueOf(index_worker);
				Integer total_worker_int = Integer.valueOf(total_worker);
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("access_passagepieton"));
				
				
				File folder = new File(conf.getAccessibilityPath());
				
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							
							String[] split = name.split("\\.");
							if(split.length == 3){
								Integer index = Integer.valueOf(split[split.length - 1]);
								
								if(index!=null && index.equals(index_worker_int)){
									access.importPassagePieton(name, 100);
								}
								
							}
							
							
						}
					}
				}

			}
			if (line.hasOption("gtfs_trip")) {

				String index_worker = line.getOptionValue("index_worker");
				String total_worker = line.getOptionValue("total_worker");

				Integer index_worker_int = Integer.valueOf(index_worker);
				Integer total_worker_int = Integer.valueOf(total_worker);
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("gtfs_trip"));
				
				
				File folder = new File(conf.getGtfsPath());
				
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							
							String[] split = name.split("\\.");
							if(split.length == 3){
								Integer index = Integer.valueOf(split[split.length - 1]);
								
								if(index!=null && index.equals(index_worker_int)){
									ratpGtfs.importTrip(name, 2000);
								}
							}
							
							
						}
					}
				}
			}
			if (line.hasOption("gtfs_stoptime")) {
				String index_worker = line.getOptionValue("index_worker");
				String total_worker = line.getOptionValue("total_worker");

				Integer index_worker_int = Integer.valueOf(index_worker);
				Integer total_worker_int = Integer.valueOf(total_worker);
				
				Pattern compile = Pattern.compile(line
						.getOptionValue("gtfs_stoptime"));
				
				
				File folder = new File(conf.getGtfsPath());
				
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						String name = fileEntry.getName();
						Matcher matcher = compile.matcher(name);
						if (matcher.find()) {
							
							String[] split = name.split("\\.");
							if(split.length == 3){
								Integer index = Integer.valueOf(split[split.length - 1]);
								
								if(index!=null && index.equals(index_worker_int)){
									ratpGtfs.importStopTime(name, 2000);
								}
							}
							
							
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
