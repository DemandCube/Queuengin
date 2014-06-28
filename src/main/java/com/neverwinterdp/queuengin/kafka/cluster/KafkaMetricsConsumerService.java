package com.neverwinterdp.queuengin.kafka.cluster;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.queuengin.MessageConsumerHandler;
import com.neverwinterdp.queuengin.MetricsConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitorable;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaMetricsConsumerService extends AbstractService implements ComponentMonitorable {
  private ComponentMonitor serviceMonitor ;
  
  private Logger logger ;
  private KafkaMessageConsumerConnector consumer ;
  private MessageConsumerHandler metricsHandler ;
  
  @Inject(optional=true) @Named("kafka:zookeeper.connect")
  private String zookeeperUrls = "127.0.0.1:2181";
  
  private String[] topic = { "metrics.consumer" } ;
  
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass()) ;
  }
  
  @Inject
  public void init(ApplicationMonitor appMonitor) {
    metricsHandler = new MetricsConsumerHandler("Kafka", appMonitor) ;
    this.serviceMonitor = appMonitor.createComponentMonitor("Kafka", getClass().getSimpleName()) ;
  }
  

  public ComponentMonitor getComponentMonitor() { return serviceMonitor ; }
  
  public void start() throws Exception {
    String consumerGroup = "metrics.consumer";
    int    numberOfThreads = 1 ;
    
    consumer = new KafkaMessageConsumerConnector(consumerGroup, zookeeperUrls) ;
    for(String selTopic : topic) {
      consumer.consume(selTopic, metricsHandler, numberOfThreads) ;
    }
  }

  public void stop() {
    logger.info("Start stop()") ;
    consumer.close(); 
    logger.info("Finish stop()") ;
  }
}