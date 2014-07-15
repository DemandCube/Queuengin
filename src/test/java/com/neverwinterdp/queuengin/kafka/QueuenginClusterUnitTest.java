package com.neverwinterdp.queuengin.kafka;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MetricsConsumerHandler;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class QueuenginClusterUnitTest {
  static KafkaClusterBuilder clusterBuilder ;

  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new KafkaClusterBuilder() ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }
  
  @Test
  public void testSendMessage() throws Exception {
    doTestSendMessage() ;
    System.out.println("\n\n**********************************************************\n\n");
    doTestSendMessage() ;
  }
  
  void doTestSendMessage() throws Exception {
    clusterBuilder.install() ;
    ApplicationMonitor appMonitor = new ApplicationMonitor("Test", "localhost") ;
    MetricsConsumerHandler handler = new MetricsConsumerHandler("Kafka", appMonitor) ;
    KafkaMessageConsumerConnector consumer = new KafkaMessageConsumerConnector("consumer", "127.0.0.1:2181") ;
    consumer.consume(KafkaClusterBuilder.TOPIC, handler, 1) ;
    
    int numOfMessages = 10000 ;
    ComponentMonitor producerMonitor = appMonitor.createComponentMonitor("KafkaMessageProducer") ;
    Map<String, String> kafkaProducerProps = new HashMap<String, String>() ;
    kafkaProducerProps.put("request.required.acks", "1");
    KafkaMessageProducer producer = new KafkaMessageProducer(kafkaProducerProps, producerMonitor, "127.0.0.1:9092") ;
    for(int i = 0 ; i < numOfMessages; i++) {
      //SampleEvent event = new SampleEvent("event-" + i, "event " + i) ;
      Message message = new Message("m" + i, new byte[1024], false) ;
      producer.send(KafkaClusterBuilder.TOPIC,  message) ;
    }
   
    Thread.sleep(2000) ;
    Assert.assertEquals(numOfMessages, handler.messageCount()) ;
    MetricFormater formater = new MetricFormater() ;
    System.out.println(formater.format(appMonitor.snapshot().getRegistry().getTimers())) ;
    //TODO: problem with consumer shutdown it seems the process is hang for 
    //awhile and produce the exception, it seems the hang problem occurs on jdk1.8 MAC OS
    consumer.close() ;
    producer.close();
    clusterBuilder.uninstall() ;
  }
}
