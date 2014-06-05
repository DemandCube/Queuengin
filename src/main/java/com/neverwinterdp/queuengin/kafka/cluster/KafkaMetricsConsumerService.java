package com.neverwinterdp.queuengin.kafka.cluster;

import org.slf4j.Logger;

import com.codahale.metrics.Counter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MessageConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.server.monitor.ComponentMonitorRegistry;
import com.neverwinterdp.server.monitor.MonitorRegistry;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaMetricsConsumerService extends AbstractService {
  private ComponentMonitorRegistry monitorRegistry ;
  
  private Logger logger ;
  private KafkaMessageConsumerConnector consumer ;
  
  @Inject(optional=true) @Named("kafka.zookeeper-urls")
  private String zookeeperUrls = "127.0.0.1:2181";
  
  private String[] topic = { "metrics.consumer" } ;
  
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass()) ;
  }
  
  public ComponentMonitorRegistry getComponentMonitorRegistry() {
    return monitorRegistry ;
  }
  
  @Inject
  public void init(MonitorRegistry mRegistry) {
    this.monitorRegistry = 
        mRegistry.createComponentMonitorRegistry("Kafka", getClass().getSimpleName()) ;
  }
  
  public void start() throws Exception {
    String consumerGroup = "metrics.consumer";
    int    numberOfThreads = 1 ;
    
    MessageConsumerHandler nullDevHandler = new NullDevConsumerHandler(monitorRegistry) ;
    consumer = new KafkaMessageConsumerConnector(consumerGroup, zookeeperUrls) ;
    for(String selTopic : topic) {
      consumer.consume(selTopic, nullDevHandler, numberOfThreads) ;
    }
  }

  public void stop() {
    logger.info("Start stop()") ;
    consumer.close(); 
    logger.info("Finish stop()") ;
  }
  
  static public class NullDevConsumerHandler implements MessageConsumerHandler {
    private ComponentMonitorRegistry registry ;
    
    NullDevConsumerHandler(ComponentMonitorRegistry registry) {
      this.registry = registry ;
    }
    
    public void onMessage(Message msg) {
      Counter counter = registry.counter(msg.getHeader().getTopic()) ;
      counter.inc(); ;
    }

    public void onErrorMessage(Message message, Throwable error) {
    }
  }
}