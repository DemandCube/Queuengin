package com.neverwinterdp.queuengin.kafka.consumer;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.queuengin.MessageConsumerHandler;
import com.neverwinterdp.queuengin.MetricsConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.yara.MetricRegistry;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaMetricsConsumerService extends AbstractService {
  private MetricRegistry metricRegistry ;
  
  private Logger logger ;
  private KafkaMessageConsumerConnector consumer ;
  private MessageConsumerHandler metricsHandler ;
  
  @Inject @Named("kafka:zookeeper.connect")
  private String zookeeperUrls = "127.0.0.1:2181";
  
  private String[] topic =  { "metrics.consumer", "metrics.tracker", "metrics.logger" } ;
  
  
  @Inject
  public void init(LoggerFactory factory, MetricRegistry metricRegistry) {
    logger = factory.getLogger(getClass()) ;
    metricsHandler = new MetricsConsumerHandler("KafkaConsumer", metricRegistry) ;
    this.metricRegistry = metricRegistry ;
  }
  
  public MetricRegistry getMetricRegistry() { return metricRegistry ; }
  
  public void start() throws Exception {
    String consumerGroup = "metrics";
    logger.info("Zookeeper connect: " + zookeeperUrls) ;
    logger.info("Listen topic: " + topic) ;
    consumer = new KafkaMessageConsumerConnector(consumerGroup, zookeeperUrls) ;
    consumer.consume(topic, metricsHandler, 1 /*numberOfThreads*/) ;
  }

  public void stop() {
    logger.info("Start stop()") ;
    consumer.close(); 
    logger.info("Finish stop()") ;
  }
}
