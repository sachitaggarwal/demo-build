package com.example.test.utils;

import java.lang.reflect.Field;

public class TestUtil {

	public void setPrivateField(Field field, Object newValue , Object instance) throws Exception {
		field.setAccessible(true);
		field.set(instance, newValue);
	}
	
}
