package com.neverwinterdp.queuengin.kafka;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.message.SampleEvent;
import com.neverwinterdp.queuengin.ReportMessageConsumerHandler;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class QueuenginClusterUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/log4j.properties") ;
  }
  
  static String TOPIC = "Queuengin" ;
  
  static protected Server  zkServer, kafkaServer ;
  static protected Shell   shell ;

  @BeforeClass
  static public void setup() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    zkServer = Server.create("-Pserver.name=zookeeper", "-Pserver.roles=zookeeper") ;
    kafkaServer = Server.create("-Pserver.name=kafka", "-Pserver.roles=kafka") ;
    
    shell = new Shell() ;
    shell.getShellContext().connect();
    shell.execute("module list --available");
    Thread.sleep(1000);
  }

  @AfterClass
  static public void teardown() throws Exception {
    shell.close() ; 
    kafkaServer.destroy();
    zkServer.destroy();
  }
  
  @Test
  public void testSendMessage() throws Exception {
    doTestSendMessage() ;
    System.out.println("\n\n**********************************************************\n\n");
    doTestSendMessage() ;
  }
  
  void doTestSendMessage() throws Exception {
    install() ;
    int numOfMessages = 10 ;
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
    producer.close();
    consumer.close() ;
    uninstall() ;
  }
  
  private void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " -Pzk:clientPort=2181 " +
        " --member-role zookeeper --autostart Zookeeper \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        
        " -Pkafka:port=9092 -Pkafka:zookeeper.connect=127.0.0.1:2181 " +
        
        " -Pkafka.zookeeper-urls=127.0.0.1:2181" +
        " -Pkafka.consumer-report.topics=" + TOPIC +
        "  --member-role kafka --autostart Kafka";
      shell.executeScript(installScript);
      Thread.sleep(1000);
  }
  
  void uninstall() {
    String uninstallScript = 
        "module uninstall --member-role zookeeper --timeout 20000 Kafka \n" +
        "module uninstall --member-role kafka --timeout 20000 Zookeeper";
    shell.executeScript(uninstallScript);
  }
}
