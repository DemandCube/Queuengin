package com.neverwinterdp.queuengin.kafka.cluster;

import com.neverwinterdp.server.service.ServiceModule;

public class ZookeeperServiceModule extends ServiceModule {
  @Override
  protected void configure() {
    bind("ZookeeperClusterService", ZookeeperClusterService.class);
  }
}