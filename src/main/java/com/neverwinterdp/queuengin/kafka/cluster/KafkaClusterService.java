package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.Time;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.service.AbstractService;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaClusterService extends AbstractService {
  @Inject
  private RuntimeEnvironment rtEnvironment; 
  
  @Inject(optional = true) @Named("kafka.config-path")
  private String kafkaConfigPath ;
  
  private KafkaServer server ;
  
  
  public void start() throws Exception {
    Properties props = loadKafkaProperties(kafkaConfigPath);
    String logDir = props.getProperty("log.dirs") ;
    logDir = logDir.replace("/", File.separator) ;
    props.setProperty("log.dirs", logDir) ;
    server = new KafkaServer(new KafkaConfig(props), new SystemTime());
    server.startup();
  }

  public void stop() {
    server.shutdown();
  }
  
  Properties loadKafkaProperties(String file) throws Exception {
    if(file == null) {
      Properties props = new Properties();
      //props.setProperty("hostname", "127.0.0.1");
      props.setProperty("port", "9092");
      props.setProperty("broker.id", Integer.toString(1));
      props.setProperty("auto.create.topics.enable", "true");
      props.setProperty("log.dirs", rtEnvironment.getDataDir() + "/server" + rtEnvironment.getServerId() + "/kafka");
      //props.setProperty("enable.zookeeper", "true");
      props.setProperty("zookeeper.connect", "127.0.0.1:2181");
      return props ;
    } else {
      String kafkaConfigPath = rtEnvironment.getConfigDir() + "/" + file ;
      Properties props = new Properties();
      props.load(new FileInputStream(kafkaConfigPath));
      String logDir = props.getProperty("log.dirs") ;
      logDir = logDir.replace("/", File.separator) ;
      props.setProperty("log.dirs", logDir) ;
      return props ;
    }
  }
  
  static public class SystemTime implements Time {
    public long milliseconds() {
      return System.currentTimeMillis();
    }
    public long nanoseconds() {
      return System.nanoTime();
    }

    public void sleep(long ms) {
      try {
        Thread.sleep(ms);
      } catch (InterruptedException e) {
      }
    }
  }
}