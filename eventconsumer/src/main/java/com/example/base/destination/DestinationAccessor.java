package com.example.base.destination;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Utility class to read destination details for cf app Note:- It is required to
 * bind destination service instance to application for below code to work
 * 
 * @author I030998
 *
 */
public class DestinationAccessor {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String AUTHORIZATION = "Authorization";
	private String destinationEnv = System.getenv("VCAP_SERVICES");
	private static final String URI = "uri"; //$NON-NLS-1$
	private static final String DESTINATION = "destination"; //$NON-NLS-1$
	private static final String CLIENT_ID = "clientid"; //$NON-NLS-1$
	private static final String CLIENT_SECRET = "clientsecret"; //$NON-NLS-1$
	private static final String URL = "url"; //$NON-NLS-1$
	private static final String DESTINATION_SERVICE_PATH = "/destination-configuration/v1/destinations/%s"; //$NON-NLS-1$
	private RestTemplate restClient = new RestTemplate();

	private static Logger logger = LoggerFactory.getLogger(DestinationAccessor.class);

	/**
	 * Get properties stored for destination in SAP CF
	 * 
	 * @param destinationName
	 * @return
	 */
	public Map<String, String> getDestinationProperties(String destinationName) {
		logger.info("Getting destination services"); //$NON-NLS-1$

		try {
			// read destination client details from environment variables of cf app
			JSONObject credentials = getCredentialsForDestinationService();
			String destinationServiceUri = credentials.getString(URI);
			String clientId = credentials.getString(CLIENT_ID);
			String clientSecret = credentials.getString(CLIENT_SECRET);
			URI xsuaaURL = new URI(credentials.getString(URL));
			if (isInvalidString(destinationServiceUri) || isInvalidString(clientId) || isInvalidString(clientSecret)
					|| isInvalidString(xsuaaURL.toString())) {
				logger.error("Failed to retrieve destination client details"); //$NON-NLS-1$
				return null;
			}

			// get JWT token for accessing destination API
			String base64encodeAuth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()); //$NON-NLS-1$
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put(AUTHORIZATION, "Basic " + base64encodeAuth); //$NON-NLS-1$ //$NON-NLS-2$
			headersMap.put("content-type", "application/x-www-form-urlencoded"); //$NON-NLS-1$ //$NON-NLS-2$
			Map<String, String> dataMap = new HashMap<>();
			dataMap.put("client_id", clientId); //$NON-NLS-1$
			dataMap.put("grant_type", "client_credentials"); //$NON-NLS-1$ //$NON-NLS-2$
			String responseBody = executePostCall(xsuaaURL.toString() + "/oauth/token", headersMap, dataMap); //$NON-NLS-1$
			String token = null;
			if (responseBody != null) {
				JSONObject json = new JSONObject(responseBody);
				if (json.has(ACCESS_TOKEN)) { //$NON-NLS-1$
					token = json.getString(ACCESS_TOKEN); //$NON-NLS-1$
				}
			}

			// get destination properties using JWT token
			if (token != null) {
				logger.debug("Got token for fetching destination"); //$NON-NLS-1$
				logger.debug("Will get destination [" + destinationName + "], uri [" + destinationServiceUri //$NON-NLS-1$ //$NON-NLS-2$
						+ "] with JWT token "); //$NON-NLS-1$
				String destinationPath = String.format(DESTINATION_SERVICE_PATH, destinationName);
				String finalDestUri = destinationServiceUri + destinationPath;
				headersMap = new HashMap<>();
				headersMap.put(AUTHORIZATION, "Bearer " + token); //$NON-NLS-1$ //$NON-NLS-2$
				responseBody = executeGetCall(finalDestUri, headersMap);

				if (responseBody != null) {
					JSONObject json = new JSONObject(responseBody);
					if (json != null && json.has("destinationConfiguration")) { //$NON-NLS-1$
						JSONObject destination = json.getJSONObject("destinationConfiguration"); //$NON-NLS-1$
						Map<String, String> props = new HashMap<>();
						Iterator<?> jsonElementsIterator = destination.keys();
						while (jsonElementsIterator.hasNext()) {
							String key = (String) jsonElementsIterator.next();
							String value = destination.getString(key);
							props.put(key, value);
						}
						logger.info("Got destination properties"); //$NON-NLS-1$
						return props;
					}
				}
			} else {
				logger.error("No JWT token retrieved"); //$NON-NLS-1$
			}
		} catch (Exception e) {
			logger.error("Failed to get destination", e); //$NON-NLS-1$
		}

		return null;
	}

	/**
	 * Get auth token for destination
	 * 
	 * @param destName
	 * @return
	 */
	public String getAuthToken(String destName) {
		String authToken = null;
		logger.info("Getting destination for - " + destName);
		Map<String, String> destProperties = getDestinationProperties(destName);
		if (destProperties != null) {
			String url = destProperties.get("URL");
			String userName = destProperties.get("User");
			String password = destProperties.get("Password");
			String cred = "Basic " + Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put("Accept", "application/json");
			headersMap.put(AUTHORIZATION, cred);
			String response = executePostCall(url, headersMap, Collections.emptyMap());
			if (response != null) {
				logger.info("Got response");
				try{
				JSONObject obj2 = new JSONObject(response);
				if (obj2.has(ACCESS_TOKEN)) {
					return obj2.getString(ACCESS_TOKEN);
				}}catch(JSONException e){
					logger.error(e.getMessage());
					return null;
				}
			}
		}
		logger.error("Getting token failed");

		return authToken;
	}

	// ***************************************************************************************
	// Environment Accessor
	// ***************************************************************************************
	private static final String VCAP_SERVICES_CREDENTIALS = "credentials"; //$NON-NLS-1$

	private JSONObject getCredentialsForDestinationService() throws JSONException {
		JSONObject jsonObj = new JSONObject(destinationEnv);
		JSONArray jsonArr = jsonObj.getJSONArray(DESTINATION);
		return jsonArr.getJSONObject(0).getJSONObject(VCAP_SERVICES_CREDENTIALS);
	}

	// ***************************************************************************************
	// HTTP Utilities
	// ***************************************************************************************

	private String executePostCall(String url, Map<String, String> parameters, Map<String, String> body) {
		try {
			MultiValueMap<String, String> headersMap = new HttpHeaders();
			if (parameters != null && parameters.size() > 0) {
				parameters.forEach((key, value) -> headersMap.add(key, value));
			}
			HttpEntity<String> requestEntity = new HttpEntity<String>(getDataString(body), headersMap);
			ResponseEntity<String> responseEntity = restClient.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (responseEntity != null) {
				return responseEntity.getBody();
			}
		} catch (HttpClientErrorException | UnsupportedEncodingException | ResourceAccessException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private String getDataString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (!result.toString().isEmpty()) {
				result.append("&"); //$NON-NLS-1$
			}
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8")); //$NON-NLS-1$
			result.append("="); //$NON-NLS-1$
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //$NON-NLS-1$
		}
		return result.toString();
	}

	private String executeGetCall(String url, Map<String, String> parameters) {
		try {
			MultiValueMap<String, String> headersMap = new HttpHeaders();
			if (parameters != null && parameters.size() > 0) {
				parameters.forEach((key, value) -> headersMap.add(key, value));
			}
			HttpEntity<String> requestEntity = new HttpEntity<String>("", headersMap); //$NON-NLS-1$
			ResponseEntity<String> responseEntity = restClient.exchange(url, HttpMethod.GET, requestEntity,
					String.class);
			return responseEntity.getBody();
		} catch (HttpClientErrorException | ResourceAccessException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private boolean isInvalidString(String object) {
		if (object == null || object.isEmpty()) {
			return true;
		}
		return false;
	}
}
