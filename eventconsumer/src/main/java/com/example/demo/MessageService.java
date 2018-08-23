package com.example.demo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.stereotype.Service;

import com.example.demo.events.BusinessPartnerEventUtil;
import com.sap.cloud.servicesdk.xbem.api.Message;
import com.sap.cloud.servicesdk.xbem.api.MessagingEndpoint;
import com.sap.cloud.servicesdk.xbem.api.MessagingException;
import com.sap.cloud.servicesdk.xbem.api.MessagingService;
import com.sap.cloud.servicesdk.xbem.connector.sapcp.MessagingServiceInfoProperties;

@Service
public final class MessageService {
	private static ConcurrentMap<String, MessagingEndpoint> BINDING_2_ENDPOINT = new ConcurrentHashMap<>();
	private static Logger LOG = LoggerFactory.getLogger(MessageService.class);
	private static final String IN_QUEUE_BINDING = "in_queue";
	private static final String OUT_QUEUE_BINDING = "out_queue";

	private List<MessageEvent> messageEvents = Collections.synchronizedList(new ArrayList<>());

	public boolean initReceiver() throws MessagingException {
		MessagingEndpoint inQueue = getMessageEndPoint(IN_QUEUE_BINDING);
		if (!inQueue.isReceiving()) {
			inQueue.receive("testclient", (stream) -> {
				stream.peek((m) -> {
					LOG.info("Message received...");
					handleMessageEvent(m);
				}).map(Message::getContent).map(MessageEvent::new).forEach(messageEvents::add);
			});
		}
		return inQueue.isReceiving();
	}

	private void handleMessageEvent(Message<byte[]> m) {
		try {
			String msg = new String(m.getContent());
			JSONObject obj;
			try {
				LOG.info("Processing message -" + msg);
				obj = new JSONObject(msg);
				if (obj != null && obj.has("EVENT_PAYLOAD")) {
					JSONObject jsonObject = obj.getJSONObject("EVENT_PAYLOAD");
					if (jsonObject != null) {
						JSONArray keys = jsonObject.getJSONArray("KEY");
						if (keys != null) {
							for (int j = 0; j < keys.length(); j++) {
								JSONObject key = keys.getJSONObject(0);
								if (key.has("BUSINESSPARTNER")) {
									String businessPartnerId = key.getString("BUSINESSPARTNER");
									BusinessPartnerEventUtil.handleBusinessPartnerEvent(businessPartnerId);
								}
							}
						} 
					}
				} else {
					LOG.error("Invalid Event Message format.");
				}
			} catch (JSONException e) {
				LOG.error("Failed to parse event." + e.getMessage());
			}

			Thread.sleep(5000);
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	public List<MessageEvent> getReceivedMessageEvents() {
		List<MessageEvent> tmp = new ArrayList<>(messageEvents);
		return Collections.unmodifiableList(tmp);
	}

	public MessageEvent sendMessage(MessageEvent event) throws MessagingException {
		getMessageEndPoint(OUT_QUEUE_BINDING).createMessage()
				.setContent(event.toJson().getBytes(StandardCharsets.UTF_8)).send();
		return event;
	}

	private synchronized MessagingEndpoint getMessageEndPoint(String binding) throws MessagingException {
		MessagingEndpoint endpoint = BINDING_2_ENDPOINT.get(binding);
		if (endpoint == null) {
			final Cloud cloud = new CloudFactory().getCloud();
			ServiceConnectorConfig config = MessagingServiceInfoProperties.init().finish();
			MessagingService messagingService = cloud.getSingletonServiceConnector(MessagingService.class, config);
			endpoint = messagingService.bind(binding).build();
			BINDING_2_ENDPOINT.put(binding, endpoint);
		} else if (endpoint.isClosed()) {
			// remove and re-connect
			BINDING_2_ENDPOINT.remove(binding);
			return getMessageEndPoint(binding);
		}
		return endpoint;
	}
}