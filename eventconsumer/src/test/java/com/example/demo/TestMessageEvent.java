package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TestMessageEvent {

	@Test
	public void testMessageEvents() {
		
		String message = "BO Created";
		MessageEvent event = new MessageEvent(message.getBytes());
		Assert.assertTrue(event.getId() != null);
		Assert.assertTrue(event.getTimestamp() != null);
		Assert.assertTrue(event.getMessage().equals(message));
		
		event = new MessageEvent(message);
		Assert.assertTrue(event.getId() != null);
		Assert.assertTrue(event.getTimestamp() != null);
		Assert.assertTrue(event.getMessage().equals(message));

		Date currentDate = new Date();
		event = new MessageEvent(message, currentDate);
		Assert.assertTrue(event.getId() != null);
		Assert.assertTrue(event.getTimestamp().equals(new SimpleDateFormat().format(currentDate)));
		Assert.assertTrue(event.getMessage().equals(message));

		event = new MessageEvent(message, null);
		Assert.assertTrue(event.getId() != null);
		Assert.assertTrue(event.getTimestamp().equals("<no_time_set>"));
		Assert.assertTrue(event.getMessage().equals(message));
		Assert.assertTrue(event.toJson().equals(message));
		
		
	}
}
