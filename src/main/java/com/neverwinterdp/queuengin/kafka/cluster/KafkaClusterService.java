package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.File;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.Time;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.neverwinterdp.server.module.ModuleProperties;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.FileUtil;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.LoggerFactory;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaClusterService extends AbstractService {
  private Logger logger ;
  private KafkaClusterServiceInfo serviceInfo ;
  private KafkaServer server ;
  
  @Inject
  public void init(LoggerFactory factory, 
                   ModuleProperties moduleProperties, 
                   KafkaClusterServiceInfo serviceInfo) throws Exception {
    logger = factory.getLogger(getClass()) ;
    this.serviceInfo = serviceInfo ;
    if(moduleProperties.isDataDrop()) {
      String logDir = serviceInfo.kafkaProperties().getProperty("log.dirs") ;
      FileUtil.removeIfExist(logDir, false);
      logger.info("module.data.drop = true, clean data directory");
    }
  }
  
  public KafkaClusterServiceInfo getServiceInfo() { return this.serviceInfo ; }
  
  public void start() throws Exception {
    Properties props = serviceInfo.kafkaProperties();
    String logDir = props.getProperty("log.dirs") ;
    logDir = logDir.replace("/", File.separator) ;
    props.setProperty("log.dirs", logDir) ;
    logger.info("kafka properties:\n" + JSONSerializer.INSTANCE.toString(props));
    
    server = new KafkaServer(new KafkaConfig(props), new SystemTime());
    server.startup();
  }

  public void stop() {
    logger.info("Start stop()");
    server.shutdown();
    logger.info("Finish stop()");
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