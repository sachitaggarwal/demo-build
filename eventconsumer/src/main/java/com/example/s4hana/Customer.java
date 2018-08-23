package com.example.s4hana;

/**
 * Customer class to hold Business Partners information available in S4Hana
 * @author I030998
 *
 */
public class Customer extends BaseEntity{

	private String customerId;
	public Customer(String customerId,String firstName, String lastName, String city, String country) {
		super(firstName, lastName, city, country);
		this.customerId = customerId;
	}

	public String getId() {
		return customerId;
	}
}
