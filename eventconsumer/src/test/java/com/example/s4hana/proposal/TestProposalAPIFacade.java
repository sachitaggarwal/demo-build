package com.example.s4hana.proposal;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;
import com.example.test.utils.TestUtil;

public class TestProposalAPIFacade {

	private TestUtil testUtil = new TestUtil();
	
	@Test
	public void testGetProposalsWithNoToken() {
		//add mocks
		APIHandler apiHandler = new APIHandler();
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("URL","https://my300497-api.s4hana.ondemand.com");
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("ProposalService")).thenReturn(testMap);
	
		List<Proposal> proposalList = new ProposalAPIFacade(destAcc,apiHandler).getNewProposals();
		Assert.assertNull(proposalList);
	}


	@Test
	public void testGetProposals() throws NoSuchFieldException, SecurityException, Exception {

		//add destination mocks
		String destinationName = "ProposalService";
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		APIHandler apiHandler = new APIHandler();
		setPrivateValue(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/"+destinationName), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/"+destinationName), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		setPrivateValue(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
	
		//add proposal api mock
		restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"d\":{\"results\" : [{\"ProposalId\" : \"11\",\"FirstName\" : \"ff\",\"LastName\" : \"ll\",\"Country\" : \"cc\",\"City\" : \"ci\",\"ApprovalStatus\" : \"created\"},{\"ProposalId\" : \"12\",\"FirstName\" : \"ff2\",\"LastName\" : \"ll2\",\"Country\" : \"cc2\",\"City\" : \"ci2\",\"ApprovalStatus\" : \"inProcess\"}]}}";
		ResponseEntity<String> responseEntity =  new ResponseEntity<String>(body,HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v2/ProposalService/ProposedCustomers"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);
		List<Proposal> proposalList = new ProposalAPIFacade(destAcc,apiHandler).getNewProposals();
		Assert.assertTrue(proposalList != null && proposalList.size() == 2);
	}

	@Test
	public void testGetProposalsErrors() throws NoSuchFieldException, SecurityException, Exception {

		//add destination mocks
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String,String> prop = new HashMap<>();
		prop.put("URL", "http://data/odata/v4/CustomerService/");
		Mockito.when(destAcc.getDestinationProperties("ProposalService")).thenReturn(prop);
		
		
		//1. no value tag in response
		APIHandler apiHandler = new APIHandler();
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"aaa\" : [{\"ProposalId\" : \"11\",\"FirstName\" : \"ff\",\"LastName\" : \"ll\",\"Country\" : \"cc\",\"City\" : \"ci\",\"ApprovalStatus\" : \"as\"},{\"ProposalId\" : \"12\",\"FirstName\" : \"ff2\",\"LastName\" : \"ll2\",\"Country\" : \"cc2\",\"City\" : \"ci2\",\"ApprovalStatus\" : \"as2\"}]}";
		ResponseEntity<String> responseEntity =  new ResponseEntity<String>(body,HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v4/CustomerService//ProposedCustomers"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);
		List<Proposal> proposalList = new ProposalAPIFacade(destAcc,apiHandler).getNewProposals();
		Assert.assertNull(proposalList);

		//2. invalid json
		apiHandler = new APIHandler();
		//mock rest call to get destinations
		restClient = Mockito.mock(RestTemplate.class);
		restClient = Mockito.mock(RestTemplate.class);
		body = "gfhfg";
		responseEntity =  new ResponseEntity<String>(body,HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v4/CustomerService//ProposedCustomers"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);
		proposalList = new ProposalAPIFacade(destAcc,apiHandler).getNewProposals();
		Assert.assertNull(proposalList);

		//3. null response
		Mockito.when(destAcc.getAuthToken("PROPOSAL_APP_OAUTH")).thenReturn("aaa");
		apiHandler = new APIHandler();
		//mock rest call to get destinations
		restClient = Mockito.mock(RestTemplate.class);
		restClient = Mockito.mock(RestTemplate.class);
		body = null;
		responseEntity =  new ResponseEntity<String>(body,HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v4/CustomerService//ProposedCustomers"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);
		proposalList = new ProposalAPIFacade(destAcc,apiHandler).getNewProposals();
		Assert.assertNull(proposalList);
		
	}

	
	@Test
	public void testRejectProposalOk() throws NoSuchFieldException, SecurityException, Exception {

		//add destination mocks
		String destinationName = "PROPOSAL_APP_OAUTH";
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		APIHandler apiHandler = new APIHandler();
		setPrivateValue(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/"+destinationName), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/PROPOSAL_APP"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		setPrivateValue(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
	
		//add proposal api mock
		restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"value\" : [{\"ProposalId\" : \"11\",\"FirstName\" : \"ff\",\"LastName\" : \"ll\",\"Country\" : \"cc\",\"City\" : \"ci\",\"ApprovalStatus\" : \"as\"},{\"ProposalId\" : \"12\",\"FirstName\" : \"ff2\",\"LastName\" : \"ll2\",\"Country\" : \"cc2\",\"City\" : \"ci2\",\"ApprovalStatus\" : \"as2\"}]}";
		ResponseEntity<String> responseEntity =  new ResponseEntity<String>(body,HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v4/CustomerService//Close"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);

		new ProposalAPIFacade(destAcc,apiHandler).closeProposal("222", "3434","duplicate data");
	}

	@Test
	public void testRejectProposalCancel() throws NoSuchFieldException, SecurityException, Exception {

		//add destination mocks
		String destinationName = "PROPOSAL_APP_OAUTH";
		String sampleDest = "{\"destination\":[{\"credentials\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"url\":\"http://data\"}}]}";
		DestinationAccessor destAcc = new DestinationAccessor();
		APIHandler apiHandler = new APIHandler();
		setPrivateValue(DestinationAccessor.class.getDeclaredField("destinationEnv"), sampleDest,destAcc);
		//mock rest call to get destinations
		RestTemplate restClient = Mockito.mock(RestTemplate.class);
		ResponseEntity<String> responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/oauth/token"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"access_token\":\"fddfdf\"}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data"), Matchers.eq(HttpMethod.POST),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/"+destinationName), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		responseBody =  new ResponseEntity<String>("{\"destinationConfiguration\":{\"uri\":\"http://test\",\"clientid\":\"sss\",\"clientsecret\":\"sss\",\"URL\":\"http://data\"}}",HttpStatus.ACCEPTED);		
		Mockito.when(restClient.exchange(Matchers.eq("http://test/destination-configuration/v1/destinations/PROPOSAL_APP"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseBody);
		setPrivateValue(DestinationAccessor.class.getDeclaredField("restClient"), restClient,destAcc);
	
		//add proposal api mock
		restClient = Mockito.mock(RestTemplate.class);
		String body = "{\"value\" : [{\"ProposalId\" : \"11\",\"FirstName\" : \"ff\",\"LastName\" : \"ll\",\"Country\" : \"cc\",\"City\" : \"ci\",\"ApprovalStatus\" : \"as\"},{\"ProposalId\" : \"12\",\"FirstName\" : \"ff2\",\"LastName\" : \"ll2\",\"Country\" : \"cc2\",\"City\" : \"ci2\",\"ApprovalStatus\" : \"as2\"}]}";
		ResponseEntity<String> responseEntity =  new ResponseEntity<String>(body,HttpStatus.FORBIDDEN);		
		Mockito.when(restClient.exchange(Matchers.eq("http://data/odata/v4/CustomerService//ProposedCustomers/Close"), Matchers.eq(HttpMethod.GET),Matchers.any(), Matchers.eq(String.class))).thenReturn(responseEntity);
		testUtil.setPrivateField(APIHandler.class.getDeclaredField("restClient"), restClient,apiHandler);

		new ProposalAPIFacade(destAcc,apiHandler).closeProposal("222", "3434","");
	}
	
	@Test
	public void testRejectProposalNoToken() throws NoSuchFieldException, SecurityException, Exception {
		//add destination mocks
		DestinationAccessor destAcc = new DestinationAccessor();
		APIHandler apiHandler = new APIHandler();
		new ProposalAPIFacade(destAcc,apiHandler).closeProposal("222", "3434","");
	}
	
	
	private static void setPrivateValue(Field field, Object newValue , Object instance) throws Exception {
        field.setAccessible(true);   
        field.set(instance, newValue);
    }
	
}
