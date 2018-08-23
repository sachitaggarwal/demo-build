//package com.example.s4hana;
//
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.example.base.destination.DestinationAccessor;
//import com.example.base.utils.APIHandler;
//
///**
// * Facade Class to access S4Hana apis via HTTP calls and using destination
// * S4Hana
// * 
// * @author I030998
// *
// */
//public class S4HanaApiFacade {
//
//	private Logger logger = LoggerFactory.getLogger(S4HanaApiFacade.class);
//	private final static String S4HANA_DEST = "S4HANA_basic";
//	private DestinationAccessor destAccessor;
//	private APIHandler apiHandler;
//	
//	public S4HanaApiFacade(DestinationAccessor accessor , APIHandler apiHandler) {
//		this.destAccessor = accessor;
//		this.apiHandler = apiHandler;
//	}
//	
//	/**
//	 * Get details for customer from S4Hana backend
//	 * 
//	 * @param customerId
//	 * @return
//	 */
//	public Customer getCustomerFromS4Hana(String customerId) {
//		String s4HanaUrl = null;
//		String credentialToken = null;
//		try {
//			//read s4hana destination
//			logger.info("Getting destination for - "+S4HANA_DEST);
//			Map<String, String> destProperties = destAccessor.getDestinationProperties(S4HANA_DEST);
//			if (destProperties != null) {
//				s4HanaUrl = destProperties.get("URL");
//				credentialToken = "Basic " + Base64.getEncoder()
//						.encodeToString((destProperties.get("User") + ":" + destProperties.get("Password")).getBytes());
//			}
//			if (s4HanaUrl == null || credentialToken == null) {
//				logger.error("Failed to read destination properties for workflow");
//				return null;
//			}
//			
//			//get customer data
//			String firstName = null;
//			String lastName = null;
//			String city = null;
//			String country = null;
//			logger.info("Getting customers from - "+s4HanaUrl);
//			Map<String, String> headersMap = new HashMap<>();
//			headersMap.put("Authorization", credentialToken);
//			headersMap.put("Accept", "application/json");
//			String response = apiHandler.executeGetCall(s4HanaUrl + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('"+customerId+"')", headersMap);
//			if(response != null) {
//				logger.debug(response);
//				JSONObject obj = new JSONObject(response);
//				JSONObject dataObject = obj.getJSONObject("d");
//				firstName = dataObject.getString("FirstName");
//				lastName = dataObject.getString("LastName");
//				//get address data
//				headersMap = new HashMap<>();
//				headersMap.put("Authorization", credentialToken);
//				headersMap.put("Accept", "application/json");
//				response = apiHandler.executeGetCall(s4HanaUrl + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('"+customerId+"')/to_BusinessPartnerAddress", headersMap);
//				if(response != null) {
//					logger.debug(response);
//					obj = new JSONObject(response);
//					dataObject = obj.getJSONObject("d").getJSONArray("results").getJSONObject(0);
//					city = dataObject.getString("CityName");
//					country = dataObject.getString("Country");
//				}
//
//				logger.info("Got customer with data - "+firstName+","+lastName+","+city+","+country);
//				return new Customer(customerId, firstName, lastName, city, country);
//			}
//		} catch (Exception e) {
//			logger.error("Fetching customer from S4Hana failed with error message - " + e.getMessage(), e);
//		}
//		
//		return null;
//	}
//
//}
