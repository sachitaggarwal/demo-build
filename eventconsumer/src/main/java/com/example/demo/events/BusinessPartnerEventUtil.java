package com.example.demo.events;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;
import com.example.s4hana.Customer;
import com.example.s4hana.CustomerApiFacade;
import com.example.s4hana.proposal.Proposal;
import com.example.s4hana.proposal.ProposalAPIFacade;

public class BusinessPartnerEventUtil {

	private static Logger LOG = LoggerFactory.getLogger(BusinessPartnerEventUtil.class);
	
	public static void handleBusinessPartnerEvent(String businessPartnerId) {
		if (businessPartnerId != null) {
			DestinationAccessor accessor = new DestinationAccessor();
			APIHandler apiHandler = new APIHandler();
			Customer customer = new CustomerApiFacade(accessor,apiHandler).getCustomerFromS4Hana(businessPartnerId);
			if (customer != null) {
				List<Proposal> proposalList = new ProposalAPIFacade(accessor,apiHandler).getNewProposals();
				if (proposalList != null) {
					proposalList.forEach(proposal -> compareProposalToCustomer(proposal, customer));
					LOG.info("Processing messages completed.");
				} else {
					LOG.info("No new proposals found.");
				}
			} else {
				LOG.error("Failed to get customer with ID:" + businessPartnerId);
			}
		} else {
			LOG.error("Invalid Event Message format.");
		}
	}

	public static void compareProposalToCustomer(Proposal proposal, Customer customer) {
		LOG.info("Comparing - " + proposal + " , " + customer);
		if (proposal.equals(customer)) {
			DestinationAccessor accessor = new DestinationAccessor();
			APIHandler apiHandler = new APIHandler();
			LOG.info("Proposal and Customer matched for id - " + proposal.getProposalId());
			if ("new".equalsIgnoreCase(proposal.getState()) || "inProcess".equalsIgnoreCase(proposal.getState())) {
				new ProposalAPIFacade(accessor,apiHandler).closeProposal("" + proposal.getProposalId(), customer.getId() , "Duplicate Business Partner - "+customer.getId()+" available in S4Hana.");
			}
		}
	}

	
}
