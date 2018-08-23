package com.example.s4hana;

import org.junit.Assert;
import org.junit.Test;


public class TestCustomer {

	@Test
	public void testEntityCompare() {
		Customer baseEntity1 = new Customer("1","Demo", "Smith", "London", "UK");
		Customer baseEntity2 = new Customer("2","demo", "Smith", "London", "UK");
		Customer baseEntity3 = new Customer("3","Demo", "Smith", "London", "UK");
		
		Assert.assertEquals(baseEntity1.getId(), "1");
		Assert.assertTrue(baseEntity1.equals(baseEntity3));
		Assert.assertFalse(baseEntity1.equals(baseEntity2));
		Assert.assertFalse(baseEntity1.equals("Demo Smith London UK"));
		
		Assert.assertTrue(baseEntity1.toString().equals("Demo Smith London UK"));
		
	}
	
}
