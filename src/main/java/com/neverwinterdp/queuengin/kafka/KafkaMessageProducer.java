package com.neverwinterdp.queuengin.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.codahale.metrics.Timer;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MessageProducer;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaMessageProducer implements MessageProducer, ComponentMonitorable {
  private ComponentMonitor componentMonitor ;
  private String name ;
  private Producer<String, String> producer;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public KafkaMessageProducer(ComponentMonitor componentMonitor, String kafkaBrokerUrls) {
    this.componentMonitor = componentMonitor ;
    Properties props = new Properties() ;
    props.put("serializer.class",     "kafka.serializer.StringEncoder");
    props.put("metadata.broker.list", kafkaBrokerUrls);
    props.put("partitioner.class", SimplePartitioner.class.getName());
    props.put("request.required.acks", "1");
    producer = new Producer<String, String>(new ProducerConfig(props));
  }

  public void send(String topic, Message msg) throws Exception {
    Timer.Context ctx = componentMonitor.timer(topic).time() ;
    String data = JSONSerializer.INSTANCE.toString(msg) ;
    producer.send(new KeyedMessage<String, String>(topic, msg.getHeader().getKey(), data));
    ctx.stop() ;
  }
  
  public void send(String topic, List<Message> messages) throws Exception {
    Timer.Context ctx = componentMonitor.timer(topic).time() ;
    List<KeyedMessage<String, String>> holder = new ArrayList<KeyedMessage<String, String>>() ;
    for(int i = 0; i < messages.size(); i++) {
      Message m = messages.get(i) ;
      String data = JSONSerializer.INSTANCE.toString(m) ;
      holder.add(new KeyedMessage<String, String>(topic, m.getHeader().getKey(), data)) ;
    }
    producer.send(holder);
    ctx.stop() ;
  }
  
  public void close() { producer.close() ; }

  public ComponentMonitor getComponentMonitor() {
    return componentMonitor; 
  }
}
