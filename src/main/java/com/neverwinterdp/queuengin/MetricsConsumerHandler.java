package com.neverwinterdp.queuengin;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class MetricsConsumerHandler implements MessageConsumerHandler {
  private int count =  0 ;
  private int errorCount = 0;
  private String module ;
  private ApplicationMonitor appMonitor ;
  
  public MetricsConsumerHandler(String module, ApplicationMonitor monitor) {
    this.module = module ;
    this.appMonitor = monitor ;
  }
  
  public int messageCount() { return count ; }
  
  public int errorMessageCount() { return errorCount ; }
  
  public void onMessage(Message msg) {
    count++ ;
  }

  public void onErrorMessage(Message message, Throwable error) {
    errorCount++ ;
  }
  
  public ComponentMonitor getComponentMonitor(String topic) {
    return appMonitor.createComponentMonitor(module, getClass().getSimpleName(), topic);
  }
}