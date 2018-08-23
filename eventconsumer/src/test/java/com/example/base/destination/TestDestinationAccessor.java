package com.example.base.destination;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.test.utils.TestUtil;

public class TestDestinationAccessor {
	
	private TestUtil testUtil = new TestUtil();
	
// 	@Test
// 	public void testScenario(){
// 		Assert.assertTrue(false);
// 	}
	
	@Test
	public void testDestinationService() throws NoSuchFieldException, SecurityException, Exception {
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/S4Hana2"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNotNull(dataMap);
		Assert.assertTrue(dataMap.get("uri")!= null);
		
	}
	
	@Test
	public void testDestinationServiceNoCrdentials() throws NoSuchFieldException, SecurityException, Exception {
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNull(dataMap);
		
	}

	@Test
	public void testDestinationServiceMissingToken() throws NoSuchFieldException, SecurityException, Exception {
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/S4Hana2"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNull(dataMap);
	}

	@Test
	public void testDestinationServiceInvalidResponse() throws NoSuchFieldException, SecurityException, Exception {
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("<>xx{",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/S4Hana2"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNull(dataMap);
	}

	@Test
	public void testExceptionsForGetCall() throws NoSuchFieldException, SecurityException, Exception {
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/S4Hana2"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenThrow(new ResourceAccessException("demo"));

		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNull(dataMap);
	}

	@Test
	public void testExceptionsForPostCall() throws NoSuchFieldException, SecurityException, Exception {
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenThrow(new ResourceAccessException("dee"));
		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
		
		Map<String, String> dataMap = destAcc.getDestinationProperties("S4Hana2");
		Assert.assertNull(dataMap);
	}

	
	@Test
	public void testExceptionForAuthToken() throws NoSuchFieldException, SecurityException, Exception{
		//mock systems.getenv
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/waste"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		
		testUtil.setPrivateField(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);

		String token = destAcc.getAuthToken("waste");
		Assert.assertNull(token);
	}
}
