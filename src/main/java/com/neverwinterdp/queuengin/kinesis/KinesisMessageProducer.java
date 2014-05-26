package com.neverwinterdp.queuengin.kinesis;

import java.util.List;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MessageProducer;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KinesisMessageProducer implements MessageProducer {
  private String name ;
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public void send(String topic, Message msg) throws Exception {
  }
  
  public void send(String topic, List<Message> messages) throws Exception {
  }
  
  public void close() { 
  }
  
}
