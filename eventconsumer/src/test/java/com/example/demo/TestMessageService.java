package com.example.demo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.example.test.utils.TestUtil;
import com.sap.cloud.servicesdk.xbem.adapter.amqp10.MessageImpl;
import com.sap.cloud.servicesdk.xbem.api.Message;
import com.sap.cloud.servicesdk.xbem.api.MessagingEndpoint;
import com.sap.cloud.servicesdk.xbem.api.MessagingException;

public class TestMessageService {

	TestUtil testUtil = new TestUtil();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMessageService() throws MessagingException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		MessagingEndpoint endpoint = Mockito.mock(MessagingEndpoint.class);
		MessageService service = new MessageService();
		Field f = MessageService.class.getDeclaredField("BINDING_2_ENDPOINT");
		f.setAccessible(true);
		Map<String, MessagingEndpoint> bindings = (Map<String, MessagingEndpoint>)f.get(null);
		bindings.put("in_queue", endpoint);
		service.initReceiver();
		Assert.assertTrue(service.getReceivedMessageEvents().size() == 0);
	}

	@Test
	public void testMessageEvent() throws MessagingException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		MessageService service = new MessageService();
		Method m = MessageService.class.getDeclaredMethod("handleMessageEvent",Message.class);
		m.setAccessible(true);
		Message<Serializable> testM = new TestMessage<byte[]>("demo");
		m.invoke(service, testM);
		
		testM = new TestMessage<byte[]>("{ }");
		m.invoke(service, testM);

		testM = new TestMessage<byte[]>("{\"EVENT_PAYLOAD\" : { \"key\": [{ \"BusinessPartnerEvent\" : \"123\"  } ]} }")  ;
		m.invoke(service, testM);

		testM = new TestMessage<byte[]>("{\"EVENT_PAYLOAD\" : { \"KEY\": [{ \"BUSINESSPARTNER\" : \"123\"  } ]} }")  ;
		m.invoke(service, testM);

	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testMessageController() throws MessagingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		MessagingEndpoint endpoint = Mockito.mock(MessagingEndpoint.class);
		MessageService service = new MessageService();
		Field f = MessageService.class.getDeclaredField("BINDING_2_ENDPOINT");
		f.setAccessible(true);
		Map<String, MessagingEndpoint> bindings = (Map<String, MessagingEndpoint>)f.get(null);
		bindings.put("in_queue", endpoint);
		bindings.put("out_queue", endpoint);
		service.initReceiver();
		
		Assert.assertTrue(service.getReceivedMessageEvents().size() == 0);
		MessageController controller = new MessageController(service);
		controller.getMessages();
		Exception e1= null;
		try{
			controller.sendMessage("ddd");
		}catch(NullPointerException e) {
			e1 = e;
		}
		Assert.assertNotNull(e1);
	
	}

	
	
	private class TestMessage<T> extends MessageImpl<Serializable>{
		
		private String content;
		
		public TestMessage(String content) {
			super("",new MessageEvent(content.getBytes()).toJson().getBytes(),null);
			this.content = content;
		}
		
		@Override
		public Serializable getContent() {
			return content.getBytes();
		}

	}
	
}
