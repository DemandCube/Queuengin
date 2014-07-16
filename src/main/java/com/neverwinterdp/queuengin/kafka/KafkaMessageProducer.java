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
    producer = new Producer<String, String>(new ProducerConfig(props));
  }

  public void send(String topic, Message msg) throws Exception {
    Timer.Context ctx = componentMonitor.timer(topic).time() ;
    String data = JSONSerializer.INSTANCE.toString(msg) ;
    producer.send(new KeyedMessage<String, String>(topic, msg.getHeader().getKey(), data));
    ctx.stop() ;
  }
  
  public KafkaMessageProducer(ComponentMonitor componentMonitor, String kafkaBrokerUrls,
		  String requiredAcks, String compressionCodec,
		  String sendBufferBytes, String producerType, String batchNumMessages,
		  String enqueueTimeout, String clientId, String requestTimeout,
		  String sendMaxRetries, String retryBackoff) {

    this.componentMonitor = componentMonitor ;
    Properties props = new Properties() ;
    props.put("serializer.class",     "kafka.serializer.StringEncoder");
    props.put("metadata.broker.list", kafkaBrokerUrls);
    props.put("partitioner.class", SimplePartitioner.class.getName());
    if(requiredAcks!= null && !requiredAcks.equals(""))
    	props.put("request.required.acks", requiredAcks);
    if(compressionCodec!= null && !compressionCodec.equals(""))
    	props.put("compression.codec", compressionCodec);
    if(sendBufferBytes!= null && !sendBufferBytes.equals(""))
    	props.put("send.buffer.bytes",sendBufferBytes);
    if (producerType!= null && producerType.equals("async")) {
    	    props.put("producer.type", "async");
    	    props.put("batch.num.messages", batchNumMessages);
    	    props.put("queue.enqueue.timeout.ms", enqueueTimeout);
    }
    if(clientId!= null && !clientId.equals(""))
    	props.put("client.id", clientId);
    if(requestTimeout!= null && !requestTimeout.equals(""))
    	props.put("request.timeout.ms", requestTimeout);
    if(sendMaxRetries!= null && !sendMaxRetries.equals(""))
    	props.put("message.send.max.retries", sendMaxRetries);
    if(retryBackoff!= null && !retryBackoff.equals(""))
    	props.put("retry.backoff.ms", retryBackoff);
    System.out.println("kafka props " +props);
    producer = new Producer<String, String>(new ProducerConfig(props));
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
