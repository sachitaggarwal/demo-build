package com.example.s4hana.proposal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;

/**
 * Facade to work with S4Hana proposal extensions
 * 
 * @author I030998
 *
 */
public class ProposalAPIFacade {

	private Logger LOG = LoggerFactory.getLogger(ProposalAPIFacade.class);
	private static final String PROPOSAL_SERVICE_DEST = "ProposalService";

	private DestinationAccessor destAcc;
	private APIHandler apiHandler;

	public ProposalAPIFacade(DestinationAccessor accessor, APIHandler apiHandler) {
		this.destAcc = accessor;
		this.apiHandler = apiHandler;
	}

	public List<Proposal> getNewProposals() {
		LOG.info("Getting proposals");

		if (destAcc != null) {
			String proposalServiceUrl = getProposalAppUrl();
			if (proposalServiceUrl != null) {
				// get propsals
				Map<String, String> headersMap = new HashMap<>();
				String response = apiHandler.executeGetCall(proposalServiceUrl + "/ProposedCustomers", headersMap);
				if (response != null) {
					try {
						List<Proposal> proposalsList = new ArrayList<>();
						JSONObject obj2 = new JSONObject(response);
						if (obj2 != null && obj2.has("d")) {
							JSONObject data = obj2.getJSONObject("d");
						if (data != null && data.has("results")) {
							JSONArray array = data.getJSONArray("results");
							for (int i = 0; i < array.length(); i++) {
								JSONObject obj = array.getJSONObject(i);
								if(obj.opt("ApprovalStatus") != JSONObject.NULL) {
									String status = obj.getString("ApprovalStatus");
									if("inProcess".equalsIgnoreCase(status) || "new".equalsIgnoreCase(status) || "created".equalsIgnoreCase(status)){
										proposalsList.add(new Proposal(obj.getString("ProposalId"), obj.getString("FirstName"),
												obj.getString("LastName"), obj.getString("City"), obj.getString("Country"),
												obj.getString("ApprovalStatus")));
									}
								}
							}
							return proposalsList;
						} }else {
							LOG.error("Failed to parse response -" + response);
						}
					} catch (JSONException e) {
						LOG.error("Failed to read proposals - " + e.getMessage());
						return null;
					}
				}
				LOG.error("Failed to read proposals null response");
				return null;
			} else {
				LOG.error("Failed to read proposal service url");
				return null;
			}
		}
		return null;
	}

	public void closeProposal(String proposalId, String customerId ,String comments) {
		LOG.info("Closing proposal - " + proposalId);
		Map<String, String> headersMap = new HashMap<>();
		int code = apiHandler.executePostCall(getProposalAppUrl() + "/Close?Comment='"+comments+"'&ProposalId=guid'"+proposalId+"'&CustomerId='"+customerId+"'", "", headersMap);
		if (code >= 200 && code <= 300) {
			LOG.info("Close completed");
		} else {
			LOG.error("Close failed");
		}
	}

	private String getProposalAppUrl() {
		LOG.info("Getting destination for - " + PROPOSAL_SERVICE_DEST);
		Map<String, String> destProperties = destAcc.getDestinationProperties(PROPOSAL_SERVICE_DEST);
		if (destProperties != null) {
			String proposalServiceUrl = destProperties.get("URL")+"/odata/v2/ProposalService";
			LOG.info("Proposal APP Url : - " + proposalServiceUrl);
			return proposalServiceUrl;
		} else {
			LOG.error("Failed to read destination properties");
			return null;
		}
	}
}
