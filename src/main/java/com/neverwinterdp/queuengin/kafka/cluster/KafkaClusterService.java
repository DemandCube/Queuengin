package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.Time;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.neverwinterdp.server.RuntimeEnvironment;
import com.neverwinterdp.server.module.ModuleProperties;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.FileUtil;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaClusterService extends AbstractService {
  private Logger logger ;
  
  @Inject
  private RuntimeEnvironment rtEnv ;
  
  @Inject
  private ModuleProperties moduleProperties; 
  
  @Inject(optional = true) @Named("kafkaPropertiesURL")
  private String kafkaPropertiesURL ;
  
  @Inject(optional = true) @Named("kafkaProperties")
  private Map<String, String> kafkaProperties ;
  
  private KafkaServer server ;
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass()) ;
  }
  
  public void start() throws Exception {
    Properties props = loadKafkaProperties(kafkaPropertiesURL);
    logger.info(
        "kafka overrided properties:\n" + 
        JSONSerializer.INSTANCE.toString(kafkaProperties)
    );
    
    if(this.kafkaProperties != null) {
      props.putAll(kafkaProperties);
    }
    
    String logDir = props.getProperty("log.dirs") ;
    logDir = logDir.replace("/", File.separator) ;
    props.setProperty("log.dirs", logDir) ;
    if(moduleProperties.isDataDrop()) {
      FileUtil.removeIfExist(logDir, false);
      logger.info("module.data.drop = true, clean data directory");
    }
    logger.info("kafka properties:\n" + JSONSerializer.INSTANCE.toString(props));
    
    server = new KafkaServer(new KafkaConfig(props), new SystemTime());
    server.startup();
  }

  public void stop() {
    logger.info("Start stop()");
    server.shutdown();
    logger.info("Finish stop()");
  }
  
  Properties loadKafkaProperties(String path) throws Exception {
    if(path == null) {
      Properties props = new Properties();
      //props.setProperty("hostname", "127.0.0.1");
      props.setProperty("port", "9092");
      props.setProperty("broker.id", Integer.toString(1));
      props.setProperty("auto.create.topics.enable", "true");
      props.setProperty("log.dirs", rtEnv.getDataDir() + "/kafka");
      //props.setProperty("enable.zookeeper", "true");
      props.setProperty("zookeeper.connect", "127.0.0.1:2181");
      return props ;
    } else {
      Properties props = new Properties();
      props.load(IOUtil.loadRes(kafkaPropertiesURL));
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