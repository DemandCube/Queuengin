package com.neverwinterdp.queuengin.kafka;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;

public class KafkaClusterBuilder {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/log4j.properties") ;
  }
  
  static String TOPIC = "metrics.consumer" ;
  
  Server  zkServer, kafkaServer ;
  Shell   shell ;

  public KafkaClusterBuilder() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    zkServer = Server.create("-Pserver.name=zookeeper", "-Pserver.roles=zookeeper") ;
    kafkaServer = Server.create("-Pserver.name=kafka", "-Pserver.roles=kafka") ;
    
    shell = new Shell() ;
    shell.getShellContext().connect();
    shell.execute("module list --type available");
    Thread.sleep(1000);
  }

  public Shell getShell() { return this.shell ; }
  
  public void destroy() throws Exception {
    shell.close() ; 
    kafkaServer.destroy();
    zkServer.destroy();
  }
  
  public void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " -Pzk:clientPort=2181 " +
        " --member-role zookeeper --autostart --module Zookeeper \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        " -Pkafka:port=9092 -Pkafka:zookeeper.connect=127.0.0.1:2181 " +
        " --member-role kafka --autostart --module Kafka \n" +
        
        "module install " +
        " -Pmodule.data.drop=true -Pkafka:zookeeper.connect=127.0.0.1:2181 " +
        " --member-role kafka --autostart --module KafkaConsumer" ;
      shell.executeScript(installScript);
      Thread.sleep(1000);
  }
  
  public void uninstall() {
    String uninstallScript = 
        "module uninstall --member-role kafka --timeout 40000 --module KafkaConsumer \n" +
        "module uninstall --member-role kafka --timeout 40000 --module Kafka \n" +
        "module uninstall --member-role zookeeper --timeout 20000 --module Zookeeper";
    shell.executeScript(uninstallScript);
  }
}