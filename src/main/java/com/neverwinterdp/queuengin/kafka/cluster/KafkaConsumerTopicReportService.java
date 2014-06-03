package com.neverwinterdp.queuengin.kafka.cluster;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.queuengin.ReportMessageConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaConsumerTopicReportService extends AbstractService {
  private Logger logger ;
  private KafkaMessageConsumerConnector consumer ;
  
  @Inject(optional=true) @Named("kafka.zookeeper-urls")
  private String zookeeperUrls = "127.0.0.1:2181";
  
  private String[] topic = {} ;
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass()) ;
  }
  
  
  @Inject(optional = true)
  public void setTopics(@Named("kafka.consumer-report.topics") String topics) {
    this.topic = topics.split(",") ;
  }
  
  public void start() throws Exception {
    String consumerGroup = "KafkaConsumerTopicReportService";
    int    numberOfThreads = 1 ;
    ReportMessageConsumerHandler handler = new ReportMessageConsumerHandler() ;
    consumer = new KafkaMessageConsumerConnector(consumerGroup, zookeeperUrls) ;
    for(String selTopic : topic) {
      consumer.consume(selTopic, handler, numberOfThreads) ;
    }
  }

  public void stop() {
    logger.info("Start stop()") ;
    consumer.close(); 
    logger.info("Finish stop()") ;
  }
}