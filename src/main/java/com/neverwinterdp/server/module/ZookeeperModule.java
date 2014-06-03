package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.queuengin.kafka.cluster.ZookeeperClusterService;

@ModuleConfig(name = "Zookeeper", autostart = true, autoInstall=false)
public class ZookeeperModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {
    bind("ZookeeperClusterService", ZookeeperClusterService.class);
  }
}