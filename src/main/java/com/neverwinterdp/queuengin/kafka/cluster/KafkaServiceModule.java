package com.neverwinterdp.queuengin.kafka.cluster;

import com.neverwinterdp.server.service.ServiceModule;

public class KafkaServiceModule extends ServiceModule {
  
  @Override
  protected void configure() {  
    bind("KafkaClusterService", KafkaClusterService.class) ;
    bind("KafkaConsumerTopicReportService", KafkaConsumerTopicReportService.class);
  }
}