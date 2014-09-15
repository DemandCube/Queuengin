package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.service.ServiceInfo;

public class KafkaClusterServiceInfo extends ServiceInfo implements Serializable {
  private Map<String, String> defaultProperties = new HashMap<String, String>();
  private Map<String, String> overridedProperties = new HashMap<String, String>();

  @Inject
  public void init(RuntimeEnvironment rtEnv, 
                   @Named("kafkaProperties") Map<String, String> overridedProperties) {
    this.overridedProperties = overridedProperties ;
    
    //props.setProperty("hostname", "127.0.0.1");
    defaultProperties.put("port", "9092");
    defaultProperties.put("broker.id", Integer.toString(Math.abs(rtEnv.getServerName().hashCode())));
    defaultProperties.put("auto.create.topics.enable", "true");
    defaultProperties.put("log.dirs", rtEnv.getDataDir() + "/kafka");
    //props.setProperty("enable.zookeeper", "true");
    defaultProperties.put("zookeeper.connect", "127.0.0.1:2181");
    defaultProperties.put("controlled.shutdown.enable", "true");
    defaultProperties.put("auto.leader.rebalance.enable", "true");
  }
  
  public Map<String, String> getDefaultProperties() { return defaultProperties ; }
  
  public Map<String, String> getOverridedProperties() {
    return this.overridedProperties ;
  }
  
  public void setOverridedProperties(Map<String, String> properties) {
    this.overridedProperties = properties;
  }
  
  public Properties kafkaProperties() {
    Properties props = new Properties() ;
    props.putAll(defaultProperties);
    if(overridedProperties != null) props.putAll(overridedProperties);
    return props ;
  }
}