package com.neverwinterdp.zookeeper.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.cluster.ClusterService;
import com.neverwinterdp.server.service.ServiceInfo;

public class ZookeeperClusterServiceInfo extends ServiceInfo implements Serializable {
  private String ipAddress ;
  private int    listenPort = 2181;
  
  private Map<String, String> defaultProperties = new HashMap<String, String>();
  
  @Inject(optional = true) @Named("zkProperties")
  private Map<String, String> zookeeperOverridedProperties = new HashMap<String, String>();

  @Inject
  public void init(RuntimeEnvironment rtEnvironment) {
    defaultProperties.put("dataDir", rtEnvironment.getDataDir()) ;
    //the port at which the clients will connect
    defaultProperties.put("clientPort", "2181") ;
    //disable the per-ip limit on the number of connections since this is a non-production config
    defaultProperties.put("maxClientCnxns", "0") ;
  }
  
  @Inject
  public void init(ClusterService service) {
    this.ipAddress = service.getMember().getIpAddress() ;
  }
  
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getListenPort() {
    return listenPort;
  }

  public void setListenPort(int listenPort) {
    this.listenPort = listenPort;
  }
  
  public String getConnect() { return ipAddress + ":" + listenPort ; }
  
  public Map<String, String> getZookeeperDefaultProperties() {
    return defaultProperties ;
  }
  
  public Map<String, String> getZookeeperOverridedProperties() {
    return this.zookeeperOverridedProperties ;
  }
  
  public void setZookeeperOverridedProperties(Map<String, String> properties) {
    this.zookeeperOverridedProperties = properties;
  }
  
  public Properties zookeeperProperties() {
    Properties props = new Properties() ;
    props.putAll(defaultProperties);
    if(this.zookeeperOverridedProperties != null) {
      props.putAll(zookeeperOverridedProperties);
    }
    return props ;
  }
}