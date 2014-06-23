package com.neverwinterdp.queuengin.kafka.cluster;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.zookeeper.server.DatadirCleanupManager;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
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
public class ZookeeperClusterService extends AbstractService {
  @Inject
  private RuntimeEnvironment rtEnvironment ;
  
  @Inject
  private ModuleProperties moduleProperties; 
  
  @Inject(optional = true) @Named("zookeeper.config-path")
  private String zookeeperConfigPath ;
  
  //@Inject(optional = true) @Named("zkProperties")
  //private Map<String, String> zkOverridedProperties ;
  
  private ZookeeperClusterServiceInfo serviceInfo ;
  
  private Logger logger ;
  private ZookeeperLaucher launcher ;
  private Thread zkThread ;

  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass().getSimpleName()) ;
  }
  
  @Inject
  public void init(ZookeeperClusterServiceInfo serviceInfo) {
    this.serviceInfo = serviceInfo ;
    getServiceRegistration().setServiceInfo(serviceInfo);
  }
  
  public void start() {
    if (launcher != null) {
      throw new IllegalStateException("ZookeeperLaucher should be null");
    }
    final Properties zkProperties = new Properties();
    try {
      if(zookeeperConfigPath != null) {
        zkProperties.load(IOUtil.loadRes(zookeeperConfigPath));
      } else {
        zkProperties.setProperty("dataDir", rtEnvironment.getDataDir()) ;
        //the port at which the clients will connect
        zkProperties.setProperty("clientPort", "2181") ;
        //disable the per-ip limit on the number of connections since this is a non-production config
        zkProperties.setProperty("maxClientCnxns", "0") ;
      }
      //Override the properties
      Map<String, String> zkOverridedProperties = serviceInfo.getZookeeperOverridedProperties() ;
      logger.info("Overrided zk properties: \n" + JSONSerializer.INSTANCE.toString(zkOverridedProperties));
      if(zkOverridedProperties != null) {
        zkProperties.putAll(zkOverridedProperties);
      }
      
      if(moduleProperties.isDataDrop()) {
        String dataDir = zkProperties.getProperty("dataDir") ;
        FileUtil.removeIfExist(dataDir, false);
        logger.info("module.data.drop = true, clean data directory");
      }
      logger.info("zookeeper config properties: \n" + JSONSerializer.INSTANCE.toString(zkProperties));
    } catch (Exception ex) {
      logger.error("Cannot lauch the ZookeeperClusterService", ex);
      return ;
    }
    
    
    zkThread = new Thread() {
      public void run() {
        try {
          launcher = create(zkProperties) ;
          launcher.start() ; 
        } catch (Exception ex) {
          launcher = null;
          logger.error("Cannot lauch the ZookeeperClusterService", ex);
          throw new RuntimeException("Cannot lauch the ZookeeperClusterService", ex);
        }
      }
    };
    zkThread.start() ;
  }

  public void stop() {
    logger.info("Start stop()");
    if (launcher != null) {
      launcher.shutdown();
      launcher = null;
    }
    logger.info("Finish stop()");
  }

  ZookeeperLaucher create(Properties zkProperties) throws ConfigException, IOException {
    QuorumPeerConfig zkConfig = new QuorumPeerConfig();
    zkConfig.parseProperties(zkProperties);
    DatadirCleanupManager purgeMgr = new DatadirCleanupManager(
        zkConfig.getDataDir(), 
        zkConfig.getDataLogDir(), 
        zkConfig.getSnapRetainCount(), 
        zkConfig.getPurgeInterval());
    purgeMgr.start();

    if (zkConfig.getServers().size() > 0) {
      return new QuorumPeerMainExt(zkConfig);
    } else {
      logger.warn(
        "Either no config or no quorum defined in config, running in standalone mode"
      );
      // there is only server in the quorum -- run as standalone
      return new ZooKeeperServerMainExt(zkConfig) ;
    }
  }
  
  static public interface ZookeeperLaucher {
    public void start() throws Exception ;
    public void shutdown() ;
  }
  
  public class QuorumPeerMainExt extends QuorumPeerMain implements ZookeeperLaucher {
    private QuorumPeerConfig zkConfig ;
    
    public QuorumPeerMainExt(QuorumPeerConfig zkConfig) {
      this.zkConfig = zkConfig ;
    }
    
    public void start() throws Exception {
      runFromConfig(zkConfig);
    }
    
    public void shutdown() {
      quorumPeer.shutdown();
    }
  }
  
  public class ZooKeeperServerMainExt extends ZooKeeperServerMain implements ZookeeperLaucher {
    private QuorumPeerConfig qConfig ;
    public  ZooKeeperServerMainExt(QuorumPeerConfig qConfig) {
      this.qConfig = qConfig ;
    }
    
    public void start() throws Exception {
      ServerConfig config = new ServerConfig();
      config.readFrom(qConfig);;
      runFromConfig(config);
    }

    public void shutdown() {
      super.shutdown();
    } 
  }
}