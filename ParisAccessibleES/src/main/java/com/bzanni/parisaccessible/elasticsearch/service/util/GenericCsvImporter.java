package com.bzanni.parisaccessible.elasticsearch.service.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public abstract class GenericCsvImporter<T> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(GenericCsvImporter.class);
	
	public abstract char delimiter();

	public void importData(String csvFile, int bulk) {
		GenericCsvImporter.LOGGER.info("start importing: "+csvFile);
		GenericCsvImporter.LOGGER.info("bulk: "+bulk);
		Date start = new Date();
		int rejected = 0;
		int pack_rejected = 0;
		int imported = 0;
		boolean first = true;
		try {
			List<T> res = new ArrayList<T>();
			CSVReader reader = new CSVReader(new FileReader(csvFile), delimiter());
			String[] list;

			while ((list = reader.readNext()) != null) {

				if (first) {
					first = false;
					continue;
				}
				List<T> obj = null;
				try {
					obj = this.convert(list);
				} catch (Exception e) {
					rejected ++;
				}

				if (obj != null) {
					res.addAll(obj);

					if (res.size() >= bulk) {

						try {
							this.savePack(res);
							imported += res.size();
							GenericCsvImporter.LOGGER.info("imported: "+imported);
							res = new ArrayList<T>();
						} catch (Exception e) {
							pack_rejected++;
						}

					}
				}
			}

			try {
				this.savePack(res);
				imported += res.size();
				
			} catch (Exception e) {
				pack_rejected++;
				GenericCsvImporter.LOGGER.error(e.getCause().getMessage());
			}
			Date end = new Date();
			GenericCsvImporter.LOGGER.info("end importing: "+csvFile);
			GenericCsvImporter.LOGGER.info("bulk: "+bulk);
			GenericCsvImporter.LOGGER.info("total imported: "+imported);
			GenericCsvImporter.LOGGER.info("rejected: "+rejected);
			GenericCsvImporter.LOGGER.info("pack rejected: "+pack_rejected);
			long time = Math.round((end.getTime() - start.getTime() )/ 1000);
			GenericCsvImporter.LOGGER.info("time: "+time+"s");
			reader.close();

		} catch (FileNotFoundException e) {
			GenericCsvImporter.LOGGER.error("", e);
		} catch (IOException e) {
			GenericCsvImporter.LOGGER.error("", e);
		}
	}

	protected abstract List<T> convert(String[] line) throws Exception;

	protected abstract void savePack(List<T> pack) throws Exception;

}
