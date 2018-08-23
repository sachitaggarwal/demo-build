package com.example.base.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestAPIHandler {

	@Test
	public void testExceptions() {
		APIHandler apiHandler = new APIHandler();
		HashMap<String, String> dummyMap = new HashMap<>();
		dummyMap.put("content-type", "application/json");
		String response = apiHandler.executeGetCall("http://test/destination-configuration/v1/destinations/S4Hana2",
				dummyMap);
		Assert.assertNull(response);

		int responseCode = apiHandler.executePostCall("http://data/oauth/token", "", dummyMap);
		Assert.assertTrue(responseCode == -1);

		response = apiHandler.executePostAndGetResponse("http://data/oauth/token", "", dummyMap);
		Assert.assertNull(response);
	}

	@Test
	public void testGetCalls() throws NoSuchFieldException, SecurityException, Exception {
		APIHandler apiHandler = new APIHandler();
		// mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}";
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, HttpStatus.ACCEPTED);
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/S4Hana2"),
				Matchers.eq(HttpMethod.GET), Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		setPrivateValue(APIHandler.class.getDeclaredField("restClient"), restClient, apiHandler);

		HashMap<String, String> dummyMap = new HashMap<>();
		dummyMap.put("content-type", "application/json");
		String response = apiHandler.executeGetCall("http://test/destination-configuration/v1/destinations/S4Hana2",
				dummyMap);
		Assert.assertTrue(body.equals(response));
	}

	@Test
	public void testPostCalls() throws NoSuchFieldException, SecurityException, Exception {
		APIHandler apiHandler = new APIHandler();
		// mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",
				HttpStatus.ACCEPTED);
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),
				Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		setPrivateValue(APIHandler.class.getDeclaredField("restClient"), restClient, apiHandler);

		HashMap<String, String> dummyMap = new HashMap<>();
		dummyMap.put("content-type", "application/json");
		int responseCode = apiHandler.executePostCall("http://data/oauth/token", "", dummyMap);
		Assert.assertTrue(responseCode == HttpStatus.ACCEPTED.value());

	}

	@Test
	public void testPostCalWithResponse() throws NoSuchFieldException, SecurityException, Exception {
		APIHandler apiHandler = new APIHandler();
		// mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"access_token\":\"fddfdf\"}";
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, HttpStatus.ACCEPTED);
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),
				Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		setPrivateValue(APIHandler.class.getDeclaredField("restClient"), restClient, apiHandler);

		HashMap<String, String> dummyMap = new HashMap<>();
		dummyMap.put("content-type", "application/json");
		String response = apiHandler.executePostAndGetResponse("http://data/oauth/token", "", dummyMap);
		Assert.assertTrue(body.equals(response));

	}

	private static void setPrivateValue(Field field, Object newValue, Object instance) throws Exception {
		field.setAccessible(true);
		field.set(instance, newValue);
	}

}
