package com.example.s4hana;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;

public class TestCustomerApiFacade {
	
	@Test
	public void testUnAuthorizedApiCall() {
		//add mocks
		APIHandler apiHandler = new APIHandler();
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("URL","https://my300497-api.s4hana.ondemand.com");
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("S4HANA2")).thenReturn(testMap);
	
		Customer customer = new CustomerApiFacade(destAcc,apiHandler).getCustomerFromS4Hana("11991199");
		Assert.assertNull(customer);
	}

	@Test
	public void testMissingURL() {
		//add mocks
		APIHandler apiHandler = new APIHandler();
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("S4HANA2")).thenReturn(testMap);
	
		Customer customer = new CustomerApiFacade(destAcc,apiHandler).getCustomerFromS4Hana("11991199");
		Assert.assertNull(customer);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInvalidS4HanaResponse() {
		//add mocks
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("URL","https://my300497-api.s4hana.ondemand.com");
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("S4HANA2")).thenReturn(testMap);
	
		APIHandler apiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(apiHandler.executeGetCall(Matchers.matches("https://my300497-api.s4hana.ondemand.com/API_BUSINESS_PARTNER/A_BusinessPartner(11991199)"),Matchers.anyMap())).thenReturn(null);

		Customer customer = new CustomerApiFacade(destAcc,apiHandler).getCustomerFromS4Hana("11991199");
		Assert.assertNull(customer);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testS4HanaResponseWithException() {
		//add mocks
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("URL","https://my300497-api.s4hana.ondemand.com");
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("S4HANA2")).thenReturn(testMap);
	
		String response = "{\"d\":{\"FirstName\": \"sas\",\"LastName\" : \"ss\",}}";
		String response2 = "xyz<dfdfd{>";
		APIHandler apiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(apiHandler.executeGetCall(Matchers.eq("https://my300497-api.s4hana.ondemand.com/API_BUSINESS_PARTNER/A_BusinessPartner('11991199')"),Matchers.anyMap())).thenReturn(response);
		Mockito.when(apiHandler.executeGetCall(Matchers.eq("https://my300497-api.s4hana.ondemand.com/API_BUSINESS_PARTNER/A_BusinessPartner('11991199')/to_BusinessPartnerAddress"),Matchers.anyMap())).thenReturn(response2);

		Customer customer = new CustomerApiFacade(destAcc,apiHandler).getCustomerFromS4Hana("11991199");
		Assert.assertNull(customer);;
	}
	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testValidS4HanaResponse() {
		//add mocks
		DestinationAccessor destAcc = Mockito.mock(DestinationAccessor.class);
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("URL","https://my300497-api.s4hana.ondemand.com");
		testMap.put("User", "XYZ");
		testMap.put("Password","123");
		Mockito.when(destAcc.getDestinationProperties("CustomerService")).thenReturn(testMap);
	
		String response = "{\"d\":{\"CustomerFirstName\": \"sas\",\"CustomerLastName\": \"ss\",\"CustomerCity\": \"ss\",\"CustomerCountry\": \"sss\"}}";
		APIHandler apiHandler = Mockito.mock(APIHandler.class);
		Mockito.when(apiHandler.executeGetCall(Matchers.eq("https://my300497-api.s4hana.ondemand.com/odata/v2/CustomerService/Customer('11991199')"),Matchers.anyMap())).thenReturn(response);

		Customer customer = new CustomerApiFacade(destAcc,apiHandler).getCustomerFromS4Hana("11991199");
		Assert.assertTrue(customer.toString().equals("sas ss ss sss"));
	}
	
}
