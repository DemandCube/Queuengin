package com.neverwinterdp.queuengin.kafka;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
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
    ApplicationMonitor appMonitor = new ApplicationMonitor("Test", "localhost") ;
    int numOfMessages = 1000000 ;
    ComponentMonitor producerMonitor = appMonitor.createComponentMonitor("KafkaMessageProducer") ;
    KafkaMessageProducer producer = new KafkaMessageProducer(producerMonitor, "127.0.0.1:9092") ;
    for(int i = 0 ; i < numOfMessages; i++) {
      Message message = new Message("m" + i, new byte[1024], false) ;
      producer.send(KafkaClusterBuilder.TOPIC,  message) ;
    }
   
    Thread.sleep(2000) ;
    clusterBuilder.getShell().execute("server metric");
    producer.close();
  }
}
