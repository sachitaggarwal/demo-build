package com.example.demo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.servicesdk.xbem.api.MessagingException;

@RestController
@RequestMapping("/")
public class MessageController {

  private static final Logger LOG = Logger.getLogger(MessageController.class.getName());
  private MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<MessageEvent> getMessages() {
    try {
      if(messageService.initReceiver()) {
        LOG.info(() -> "MessageService receiver is active.");
      }
    } catch (MessagingException e) {
      LOG.info(() -> "MessageService receiver init failed: " + e.getMessage());
    }
    return messageService.getReceivedMessageEvents();
  }

  @PostMapping(value = "/messages", consumes = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<?> sendMessage(@RequestBody String content) {
    try {
      LOG.info("REquest content - "+content);	
      messageService.sendMessage(new MessageEvent(content));
      return ResponseEntity.accepted().build();
    } catch (MessagingException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);	
      return ResponseEntity.badRequest().build();
    }
  }
}