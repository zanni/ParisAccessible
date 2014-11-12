package com.bzanni.parisaccessible.indexer.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Path;
import com.bzanni.parisaccessible.neo.service.BatchInserterService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IndexWorkerSyncService {

	private static final long delay = 3 * 60 * 1000;

	@Autowired
	private ConfigurableApplicationContext context;

	@Resource
	private BatchInserterService batchService;

	@Resource
	private MailSenderService mailService;

	private ObjectMapper mapper;

	private List<Integer> ackWorker = new ArrayList<Integer>();

	private List<Integer> ackWorkerEnd = new ArrayList<Integer>();

	private Map<Integer, Timer> scheduled = new HashMap<Integer, Timer>();

	private Integer total_worker;
	private JavaType type;
	private Date start;

	public String subject;

	private class WorkerDied extends TimerTask {

		public Integer index_worker;

		public WorkerDied(Integer index) {
			this.index_worker = index;

		}

		@Override
		public void run() {
			try {
				boolean expected = ackWorkerEnd.contains(index_worker);

				if (!expected) {
					ackWorkerEnd.add(index_worker);
					mailService.send(subject, "unexpected worker died "
							+ index_worker);
				} else {
					mailService.send(subject, "receive stop from worker:"
							+ index_worker);
				}
				if (ackWorkerEnd.size() == total_worker) {
					Date end = new Date();
					mailService.send(
							subject,
							"stop indexing time: "
									+ Math.round((end.getTime() - start
											.getTime()) / 60 / 1000)
									+ "min, nodes: " + batchService.getNodes()
									+ ", relationships: "
									+ batchService.getRelationships());
					context.close();
					System.exit(0);
				}

			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void ackWorker(Integer worker) {
		ackWorker.add(worker);
		if (scheduled.get(worker) != null) {
			scheduled.get(worker).cancel();
		}
		Timer timer = new Timer(true);
		timer.schedule(new WorkerDied(worker), delay);
		scheduled.put(worker, timer);

	}

	public IndexWorkerSyncService() {

		mapper = new ObjectMapper();

		type = mapper.getTypeFactory().constructCollectionType(
				Collection.class, Path.class);

	}

	public void receivePath(Message message) throws JsonParseException,
			JsonMappingException, IOException {
		String key = message.getMessageProperties().getReceivedRoutingKey();

		Integer worker_index = Integer.valueOf(key.split("\\.")[1]);
		ackWorker(worker_index);
		String str = "";
		byte[] body = message.getBody();
		str = new String(body);
		List<Path> readValue = mapper.readValue(str, type);
		batchService.addBidirectionalToInserter(readValue);
	}

	public void receiveWorflow(Message message) throws IOException,
			AddressException, MessagingException {
		byte[] body = message.getBody();

		Map<String, Object> map = mapper.readValue(new String(body), Map.class);

		if (map.get("cycle").equals("start")) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			start = new Date();
			subject = "Indexing " + format.format(start);
			Integer s = (Integer) map.get("index_worker");

			ackWorker(s);
			total_worker = (Integer) map.get("total_worker");
			mailService.send(subject, "receive start from worker:" + s + " over "+total_worker);

			

			if (ackWorker.size() == total_worker) {
				mailService.send(subject, "start indexing with " + total_worker
						+ " workers");
			}
		}

		if (map.get("cycle").equals("end")) {

			Integer s = (Integer) map.get("index_worker");
			total_worker = (Integer) map.get("total_worker");
			ackWorkerEnd.add(s);

		}
	}

}
