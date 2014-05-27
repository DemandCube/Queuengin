package com.neverwinterdp.queuengin.kafka.cluster;

import java.util.Properties;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.neverwinterdp.server.Server;
import com.neverwinterdp.util.IOUtil;

public class KafkaServer {
  static public class Options {
    @Parameter(
      names = "-config", 
      description = "The configuration file in the properties format"
    )
    String configFile;
    
    @Parameter(
       names = "-zk-connect", 
       description = "The list of the zookeeper server in fomat host:port, separate by comma. ex 127.0.0.1:2181"
    )
    String zkConnect;
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
      properties.put("server.service-module", KafkaServiceModule.class.getName()) ;
      properties.put("kafka.zookeeper-urls", "127.0.0.1:2181") ;
    }
    //zkServerProps.put("zookeeper.config-path", "") ;
    if(options.zkConnect != null) {
      properties.put("kafka.zookeeper-urls", options.zkConnect) ;
    }
    Server zkServer = Server.create(properties);
    Thread.currentThread().join();
  }
}