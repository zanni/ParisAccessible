package com.bzanni.parisaccessible.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.service.ShortestPathService;

@Controller
public class ShortestPathController {

	@Resource
	private ShortestPathService shortestPathService;

	@Value("${parisaccessible_app_datafolder}")
	private String dataFolder;

	@PostConstruct
	public void init() {
		shortestPathService.init(dataFolder);
	}

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		return "map";
	}

	@RequestMapping(value = "/path", method = RequestMethod.GET)
	@ResponseBody
	public List<Location> findShortestPath(
			@RequestParam("start_lat") Double start_lat,
			@RequestParam("start_lon") Double start_lon,
			@RequestParam("end_lat") Double end_lat,
			@RequestParam("end_lon") Double end_lon) {

		List<Location> findShortestPath = shortestPathService.findShortestPath(
				Arrays.asList(start_lon, start_lat),
				Arrays.asList(end_lon, end_lat));

		return findShortestPath;

	}

	@RequestMapping(value = "/location", method = RequestMethod.GET)
	@ResponseBody
	public Location findShortestPath(@RequestParam("lat") Double lat,
			@RequestParam("lon") Double lon) {

		return shortestPathService.findLocation(Arrays.asList(lat, lon));

	}
}
