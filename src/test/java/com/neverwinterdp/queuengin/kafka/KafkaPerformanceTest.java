package com.neverwinterdp.queuengin.kafka;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.yara.MetricRegistry;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaPerformanceTest {
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
  public void testPerformance() throws Exception {
    clusterBuilder.install() ;
    MetricRegistry mRegistry = new MetricRegistry("localhost") ;
    int numOfMessages = 10000 ;
    Map<String, String> kafkaProducerProps = new HashMap<String, String>() ;
    kafkaProducerProps.put("request.required.acks", "1");
    KafkaMessageProducer producer = new KafkaMessageProducer(kafkaProducerProps, mRegistry, "127.0.0.1:9092") ;
    for(int i = 0 ; i < numOfMessages; i++) {
      Message message = new Message("m" + i, new byte[1024], false) ;
      producer.send(KafkaClusterBuilder.TOPIC,  message) ;
    }
   
    producer.close();
    Thread.sleep(2000) ;
    clusterBuilder.getShell().execute("server metric");
    clusterBuilder.uninstall();
  }
}
