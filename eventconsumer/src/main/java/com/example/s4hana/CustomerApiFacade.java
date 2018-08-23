package com.example.s4hana;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;

/**
 * Facade Class to access customers using customer service
 * S4Hana
 * 
 * @author I030998
 *
 */
public class CustomerApiFacade {

	private Logger logger = LoggerFactory.getLogger(CustomerApiFacade.class);
	private final static String CUSTOMER_DESTINATION = "CustomerService";
	private DestinationAccessor destAccessor;
	private APIHandler apiHandler;
	
	public CustomerApiFacade(DestinationAccessor accessor , APIHandler apiHandler) {
		this.destAccessor = accessor;
		this.apiHandler = apiHandler;
	}
	
	/**
	 * Get details for customer from S4Hana backend
	 * 
	 * @param customerId
	 * @return
	 */
	public Customer getCustomerFromS4Hana(String customerId) {
		String s4HanaUrl = null;
		String credentialToken = null;
		try {
			//read s4hana destination
			logger.info("Getting destination for - "+CUSTOMER_DESTINATION);
			Map<String, String> destProperties = destAccessor.getDestinationProperties(CUSTOMER_DESTINATION);
			if (destProperties != null) {
				s4HanaUrl = destProperties.get("URL");
				credentialToken = "Basic " + Base64.getEncoder()
						.encodeToString((destProperties.get("User") + ":" + destProperties.get("Password")).getBytes());
			}
			if (s4HanaUrl == null || credentialToken == null) {
				logger.error("Failed to read destination properties for workflow");
				return null;
			}
			
			//get customer data
			String firstName = null;
			String lastName = null;
			String city = null;
			String country = null;
			logger.info("Getting customers from - "+s4HanaUrl);
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put("Authorization", credentialToken);
			headersMap.put("Accept", "application/json");
			String response = apiHandler.executeGetCall(s4HanaUrl + "/odata/v2/CustomerService/Customer('"+customerId+"')", headersMap);
			if(response != null) {
				logger.debug(response);
				JSONObject obj = new JSONObject(response);
				JSONObject dataObject = obj.getJSONObject("d");
				firstName = dataObject.getString("CustomerFirstName");
				lastName = dataObject.getString("CustomerLastName");
				city = dataObject.getString("CustomerCity");
				country = dataObject.getString("CustomerCountry");
				logger.info("Got customer with data - "+firstName+","+lastName+","+city+","+country);
				return new Customer(customerId, firstName, lastName, city, country);
			}
		} catch (Exception e) {
			logger.error("Fetching customer from S4Hana failed with error message - " + e.getMessage(), e);
		}
		
		return null;
	}

}
