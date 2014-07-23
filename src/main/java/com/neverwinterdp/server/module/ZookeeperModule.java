package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.zookeeper.cluster.ZookeeperClusterService;

@ModuleConfig(name = "Zookeeper", autostart = false, autoInstall=false)
public class ZookeeperModule extends ServiceModule {
  protected void configure(Map<String, String> properties) {
    bind("ZookeeperClusterService", ZookeeperClusterService.class);
  }
}