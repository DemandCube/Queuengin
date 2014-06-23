package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.cluster.ClusterService;

public class ZookeeperClusterServiceInfo implements Serializable {
  private String ipAddress ;
  private int    listenPort = 2181;

  @Inject(optional = true) @Named("zkProperties")
  private Map<String, String> zookeeperOverridedProperties = new HashMap<String, String>();

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
  
  public Map<String, String> getZookeeperOverridedProperties() {
    return this.zookeeperOverridedProperties ;
  }
  
  public void setZookeeperOverridedProperties(Map<String, String> properties) {
    this.zookeeperOverridedProperties = properties;
  }
}