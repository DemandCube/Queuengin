package com.neverwinterdp.queuengin;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.yara.MetricRegistry;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class MetricsConsumerHandler implements MessageConsumerHandler {
  private int count =  0 ;
  private int errorCount = 0;
  private String module ;
  private MetricRegistry metricRegistry ;
  
  public MetricsConsumerHandler(String module, MetricRegistry registry) {
    this.module = module ;
    this.metricRegistry = registry ;
  }
  
  public int messageCount() { return count ; }
  
  public int errorMessageCount() { return errorCount ; }
  
  public void onMessage(Message msg) {
    count++ ;
  }

  public void onErrorMessage(Message message, Throwable error) {
    errorCount++ ;
  }
  
  public MetricRegistry getMetricRegistry() { return metricRegistry; }
}