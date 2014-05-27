package com.neverwinterdp.queuengin.kafka;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.message.SampleEvent;
import com.neverwinterdp.queuengin.ReportMessageConsumerHandler;
import com.neverwinterdp.queuengin.kafka.cluster.KafkaServiceModule;
import com.neverwinterdp.queuengin.kafka.cluster.ZookeeperServiceModule;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.cluster.hazelcast.HazelcastClusterClient;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class QueuenginClusterUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/kafka/log4j.properties") ;
  }
  
  static String TOPIC = "Queuengin" ;
  
  static protected Server      zkServer, kafkaServer ;
  static protected ClusterClient client ;

  @BeforeClass
  static public void setup() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    Properties zkServerProps = new Properties() ;
    zkServerProps.put("server.group", "NeverwinterDP") ;
    zkServerProps.put("server.cluster-framework", "hazelcast") ;
    zkServerProps.put("server.roles", "master") ;
    zkServerProps.put("server.service-module", ZookeeperServiceModule.class.getName()) ;
    //zkServerProps.put("zookeeper.config-path", "") ;
    zkServer = Server.create(zkServerProps);
    
    Properties kafkaServerProps = new Properties() ;
    kafkaServerProps.put("server.group", "NeverwinterDP") ;
    kafkaServerProps.put("server.cluster-framework", "hazelcast") ;
    kafkaServerProps.put("server.roles", "master") ;
    kafkaServerProps.put("server.service-module", KafkaServiceModule.class.getName()) ;
    kafkaServerProps.put("kafka.zookeeper-urls", "127.0.0.1:2181") ;
    kafkaServerProps.put("kafka.consumer-report.topics", TOPIC) ;
    kafkaServer = Server.create(kafkaServerProps);
    
    ClusterMember member = zkServer.getClusterService().getMember() ;
    String connectUrl = member.getIpAddress() + ":" + member.getPort() ;
    client = new HazelcastClusterClient(connectUrl) ;
    Thread.sleep(1000);
  }

  @AfterClass
  static public void teardown() throws Exception {
    client.shutdown(); 
    zkServer.exit(0) ;
  }
  
  @Test
  public void testSendMessage() throws Exception {
    int numOfMessages = 150 ;
    KafkaMessageProducer producer = new KafkaMessageProducer("127.0.0.1:9092") ;
    for(int i = 0 ; i < numOfMessages; i++) {
      SampleEvent event = new SampleEvent("event-" + i, "event " + i) ;
      Message jsonMessage = new Message("m" + i, event, false) ;
      producer.send(TOPIC,  jsonMessage) ;
    }
   
    ReportMessageConsumerHandler handler = new ReportMessageConsumerHandler() ;
    KafkaMessageConsumerConnector consumer = new KafkaMessageConsumerConnector("consumer", "127.0.0.1:2181") ;
    consumer.consume(TOPIC, handler, 1) ;
    Thread.sleep(2000) ;
    Assert.assertEquals(numOfMessages, handler.messageCount()) ;
  }
}
