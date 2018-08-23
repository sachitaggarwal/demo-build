//package com.example.zintegration.tests;
//
//import java.util.Collections;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.example.base.utils.APIHandler;
//
//public class ITMessageServiceTest {
//
//	private static final String TEST_URL = "https://persephone-event-consumer-test.cfapps.eu10.hana.ondemand.com/messages";
//
//	@Test
//	public void testSendRecieveMessage() throws InterruptedException {
//		APIHandler handler = new APIHandler();
//		String response = handler.executeGetCall(TEST_URL, Collections.emptyMap());
//		Assert.assertTrue("After fresh deploy messages list not empty", "[]".equals(response));
//		
//		//send message
//		String testMessage = "{\"EVENT_PAYLOAD\": {\"KEY\": [{\"BUSINESSPARTNER\": \"123\"}]}}";
//		int responseCode = handler.executePostCall(TEST_URL, testMessage, Collections.emptyMap());
//		Assert.assertTrue("Sending message to queue failed",responseCode == 202);
//		
//		//wait
//		Thread.sleep(5000);
//		response = handler.executeGetCall(TEST_URL, Collections.emptyMap());
//		if(!response.contains("123")) {
//			Thread.sleep(5000);
//			response = handler.executeGetCall(TEST_URL, Collections.emptyMap());
//			Assert.assertTrue("No message recieved even after 10 seconds",response.contains("123"));
//		}
//	
//	}
//	
//}
