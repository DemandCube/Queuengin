package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.queuengin.kafka.cluster.KafkaClusterService;
import com.neverwinterdp.queuengin.kafka.cluster.KafkaMetricsConsumerService;

@ModuleConfig(name = "Kafka", autostart = false, autoInstall=false)
public class KafkaModule extends ServiceModule {
  
  protected void configure(Map<String, String> properties) {  
    bindService(KafkaMetricsConsumerService.class);
    bindService(KafkaClusterService.class) ;
  }
}