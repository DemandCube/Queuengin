package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.File;
import java.util.Map;
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
  ThreadGroup kafkaGroup ;
  int kafkaGroupTracker = 1 ;
  
  @Inject
  public void init(LoggerFactory factory, 
                   ModuleProperties moduleProperties, 
                   KafkaClusterServiceInfo serviceInfo) throws Exception {
    logger = factory.getLogger(getClass()) ;
    this.serviceInfo = serviceInfo ;
    if(moduleProperties.isDataDrop()) {
      cleanup() ;
    }
  }
  
  public boolean configure(Map<String, String> properties) throws Exception {
    serviceInfo.getOverridedProperties().putAll(properties);
    return true ;
  }
  
  public KafkaClusterServiceInfo getServiceInfo() { return this.serviceInfo ; }
  
  public boolean cleanup() throws Exception {
    String logDir = serviceInfo.kafkaProperties().getProperty("log.dirs") ;
    FileUtil.removeIfExist(logDir, false);
    logger.info("Clean kafka data directory");
    return true ;
  }
  
  public void start() throws Exception {
    logger.info("Start start()");
    long startTime = System.currentTimeMillis() ;
    final Properties props = serviceInfo.kafkaProperties();
    kafkaGroup = new ThreadGroup("Kafka-" + props.getProperty("broker.id") + "-" + ++kafkaGroupTracker) ;
    String logDir = props.getProperty("log.dirs") ;
    logDir = logDir.replace("/", File.separator) ;
    props.setProperty("log.dirs", logDir) ;
    
    logger.info("kafka overrided properties:\n" + JSONSerializer.INSTANCE.toString(serviceInfo.getOverridedProperties()));
    logger.info("kafka properties:\n" + JSONSerializer.INSTANCE.toString(props));
    
    Thread thread = new Thread(kafkaGroup, "KafkaLauncher") {
      public void run() {
        server = new KafkaServer(new KafkaConfig(props), new SystemTime());
        server.startup();
      }
    };
    thread.start() ;
    logger.info("Start KafkaClusterService in " + (System.currentTimeMillis() - startTime) + "ms");
    logger.info("Finish start()");
  }

  public void stop() {
    logger.info("Start stop()");
    long startTime = System.currentTimeMillis() ;
    //TODO: check and remove this work around when upgrade to the next version of kafka
    //https://issues.apache.org/jira/browse/KAFKA-1574
    //use the kafka thread killer to kill all the kafka thread.
    KafkaThreadKiller thread  = new KafkaThreadKiller() ;
    thread.start();
    logger.info("Shutdown KafkaClusterService in " + (System.currentTimeMillis() - startTime) + "ms");
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
 
  
  //https://issues.apache.org/jira/browse/KAFKA-1305
  public class KafkaThreadKiller extends Thread {
    public void run() {
      if(server == null) return ;
      //server.awaitShutdown();
      //server.socketServer().shutdown();
      //server.kafkaController().shutdown();
      //server.kafkaScheduler().shutdown();
      //server.replicaManager().shutdown() ;
      //kafkaGroup.interrupt() ;
      server.shutdown();
      //server.kafkaController().shutdown();
      //server.replicaManager().replicaFetcherManager().closeAllFetchers();
      //server.kafkaScheduler().shutdown();
      //server.logManager().shutdown();
      kafkaGroup.interrupt() ;
      kafkaGroup = null ;
      server = null ;
      logger.info("KafkaThreadKiller thread shutdown kafka successfully");
    }
  }
}