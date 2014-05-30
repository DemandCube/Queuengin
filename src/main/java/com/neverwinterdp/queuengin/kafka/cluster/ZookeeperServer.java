package com.neverwinterdp.queuengin.kafka.cluster;

import java.util.Properties;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.util.IOUtil;

public class ZookeeperServer {
  static public class Options {
    @Parameter(names = "-config", description = "The configuration file in the properties format")
    String configFile;
  }
  
  static public void main(String[] args) throws Exception {
    Options options = new Options();
    new JCommander(options, args);
    Properties properties = new Properties() ;
    if(options.configFile != null) {
      properties.load(IOUtil.loadRes(options.configFile));
    } else {
      properties.put("server.group", "NeverwinterDP") ;
      properties.put("server.cluster-framework", "hazelcast") ;
      properties.put("server.roles", "master") ;
      properties.put("server.available-modules", ZookeeperServiceModule.class.getName()) ;
      properties.put("server.install-modules", ZookeeperServiceModule.class.getName()) ;
      properties.put("server.install-modules-autostart", "true") ;
    }
    //zkServerProps.put("zookeeper.config-path", "") ;
    Server zkServer = Server.create(properties);
    Thread.currentThread().join();
  }
}