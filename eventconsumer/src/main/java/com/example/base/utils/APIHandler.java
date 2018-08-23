package com.example.base.utils;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Rest API client to fire rest calls
 * 
 * @author I030998
 *
 */
public class APIHandler {

	private RestTemplate restClient = new RestTemplate();
	private static final Logger LOG = Logger.getLogger(APIHandler.class.getName());

	public APIHandler() {
	}

	public String executeGetCall(String url, Map<String, String> parameters) {
		try {
			MultiValueMap<String, String> headersMap = new HttpHeaders();
			if (parameters != null && parameters.size() > 0) {
				parameters.forEach((key, value) -> headersMap.add(key, value));
			}
			HttpEntity<String> requestEntity = new HttpEntity<String>("", headersMap); //$NON-NLS-1$
			ResponseEntity<String> responseEntity = restClient.exchange(url, HttpMethod.GET, requestEntity,
					String.class);
			if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
				return responseEntity.getBody();
			}
		} catch (HttpClientErrorException | ResourceAccessException e) {
			LOG.info(e.getMessage());
		}
		return null;
	}

	public int executePostCall(String url, String body, Map<String, String> parameters) {
		try {
			MultiValueMap<String, String> headersMap = new HttpHeaders();
			if (parameters != null && parameters.size() > 0) {
				parameters.forEach((key, value) -> headersMap.add(key, value));
			}
			HttpEntity<String> requestEntity = new HttpEntity<String>(body, headersMap);
			ResponseEntity<String> responseEntity = restClient.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			return responseEntity.getStatusCodeValue();
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
		return -1;
	}

	public String executePostAndGetResponse(String url, String body, Map<String, String> parameters) {
		try {
			MultiValueMap<String, String> headersMap = new HttpHeaders();
			if (parameters != null && parameters.size() > 0) {
				parameters.forEach((key, value) -> headersMap.add(key, value));
			}
			HttpEntity<String> requestEntity = new HttpEntity<String>(body, headersMap);
			ResponseEntity<String> responseEntity = restClient.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
		return null;
	}
	
}
