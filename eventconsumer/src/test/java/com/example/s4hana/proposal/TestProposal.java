package com.example.s4hana.proposal;

import org.junit.Assert;
import org.junit.Test;


public class TestProposal {

	@Test
	public void testEntityCompare() {
		Proposal baseEntity1 = new Proposal("1","Demo", "Smith", "London", "UK","inprocess");
		Proposal baseEntity2 = new Proposal("2","demo", "Smith", "London", "UK","completed");
		Proposal baseEntity3 = new Proposal("3","Demo", "Smith", "London", "UK","new");
		
		Assert.assertEquals(baseEntity1.getProposalId(), "1");
		Assert.assertEquals(baseEntity1.getState(), "inprocess");
		Assert.assertTrue(baseEntity1.equals(baseEntity3));
		Assert.assertFalse(baseEntity1.equals(baseEntity2));
		Assert.assertFalse(baseEntity1.equals("Demo Smith London London"));
		
		Assert.assertTrue(baseEntity1.toString().equals("Demo Smith London UK"));
		
	}
	
}
