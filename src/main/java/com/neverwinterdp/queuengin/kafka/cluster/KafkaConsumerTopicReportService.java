package com.neverwinterdp.queuengin.kafka.cluster;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.queuengin.ReportMessageConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.server.service.AbstractService;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaConsumerTopicReportService extends AbstractService {
  private KafkaMessageConsumerConnector consumer ;
  
  @Inject(optional=true) @Named("kafka.zookeeper-urls")
  private String zookeeperUrls = "127.0.0.1:2181";
  
  private String[] topic = {} ;
  
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
    consumer.close(); 
  }
}