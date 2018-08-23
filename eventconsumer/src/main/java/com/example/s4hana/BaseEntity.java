package com.example.s4hana;
/**
 * Base entity to hold customers , proposals
 * @author I030998
 *
 */
public class BaseEntity {

	private String firstName , lastName , city , country;
	
	public BaseEntity(String firstName , String lastName , String city , String country) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.country = country;		
	}
	
	public int hashCode() {
		StringBuilder url = new StringBuilder();
		url.append((firstName != null ? firstName : ""));
		url.append((lastName != null ? lastName : ""));
		url.append((city != null ? city : ""));
		url.append((country != null ? country : ""));
		return url.toString().hashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof BaseEntity) {
			int objHashCode = ((BaseEntity)obj).hashCode();
			return this.hashCode() == objHashCode;
		}
		return false;
	}
	
	public String toString() {
		return firstName +" " +lastName+" "+city+ " "+country;
	}
}
