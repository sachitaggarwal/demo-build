package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

public class MessageEvent {
  final String uuid;
  final String message;
  final Date timestamp;
  private static final Logger LOG = Logger.getLogger(MessageEvent.class.getName());

  public MessageEvent(byte[] content) {
    this(new String(content), new Date());
  }

  public MessageEvent(String content) {
    this(content, new Date());
  }

  public MessageEvent(String message, Date timestamp) {
	LOG.info("Creating msg - "+message);  
    this.message = message;
    this.timestamp = timestamp;
    this.uuid = UUID.randomUUID().toString();
  }

  public String getMessage() {
    return message;
  }

  public String getTimestamp() {
    if(timestamp != null) {
      SimpleDateFormat sdf = new SimpleDateFormat();
      return sdf.format(timestamp);
    }
    return "<no_time_set>";
  }

  public String getId() {
    return uuid;
  }

  public String toJson() {
    return message;
  }
}