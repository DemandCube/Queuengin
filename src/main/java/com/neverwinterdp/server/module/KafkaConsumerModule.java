package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.queuengin.kafka.consumer.KafkaMetricsConsumerService;

@ModuleConfig(name = "KafkaConsumer", autostart = false, autoInstall=false)
public class KafkaConsumerModule extends ServiceModule {
  
  protected void configure(Map<String, String> properties) {  
    bindService(KafkaMetricsConsumerService.class);
  }
}