package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.stereotype.Service;

@Service
public class JestQueryEngine {

	private VelocityEngine engine;

	public JestQueryEngine() {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine = new VelocityEngine(props);
//		engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
//				"classpath:/META-INF/template");
	}

	private Map<String, Template> cache = new HashMap<String, Template>();

	private Template retreiveTemplate(String template) {
		Template t = cache.get(template);
		if (t == null) {
			t = engine.getTemplate(template);
		}
		return t;
	}

	public String putMappingQuery(Class<?> klass) {
		Template template = retreiveTemplate("/META-INF/template/mapping/"
				+ klass.getSimpleName().toLowerCase() + ".vm");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		template.merge(context, writer);
		return writer.toString();
	}

	public String matchAllQuery() {
		Template template = retreiveTemplate("/META-INF/template/query/matchAllQuery.vm");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		template.merge(context, writer);
		return writer.toString();
	}

	public String termQuery(String field, String value) {
		Template template = retreiveTemplate("/META-INF/template/query/termQuery.vm");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		context.put("field", field);
		context.put("value", value);
		template.merge(context, writer);
		return writer.toString();
	}

	public String geoDistanceQuery(String field, Double lat, Double lon,
			String distance) {
		Template template = retreiveTemplate("/META-INF/template/query/geoDistanceQuery.vm");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		context.put("field", field);
		context.put("lat", lat);
		context.put("lon", lon);
		context.put("distance", distance);
		template.merge(context, writer);
		return writer.toString();
	}

	public String geoShapeDistanceQuery(String field, Double lat, Double lon,
			String distance) {
		Template template = retreiveTemplate("/META-INF/template/query/geoShapeDistanceQuery.vm");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		context.put("field", field);
		context.put("lat", lat);
		context.put("lon", lon);
		context.put("distance", distance);
		template.merge(context, writer);
		return writer.toString();
	}
}
