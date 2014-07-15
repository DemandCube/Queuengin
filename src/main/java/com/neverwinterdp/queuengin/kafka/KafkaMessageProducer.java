package com.neverwinterdp.queuengin.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    this(null, componentMonitor, kafkaBrokerUrls) ;
  }
  
  public KafkaMessageProducer(Map<String, String> props, ComponentMonitor componentMonitor, String kafkaBrokerUrls) {
    this.componentMonitor = componentMonitor ;
    Properties kafkaProps = new Properties() ;
    kafkaProps.put("serializer.class",     "kafka.serializer.StringEncoder");
    kafkaProps.put("partitioner.class", SimplePartitioner.class.getName());
    kafkaProps.put("request.required.acks", "1");
    if(props != null) {
      kafkaProps.putAll(props);
    }
    kafkaProps.put("metadata.broker.list", kafkaBrokerUrls);
    producer = new Producer<String, String>(new ProducerConfig(kafkaProps));
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
