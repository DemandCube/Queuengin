package com.neverwinterdp.queuengin ;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.yara.MetricRegistry;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface MessageConsumerHandler {
  public void onMessage(Message message)  ;
  public void onErrorMessage(Message message, Throwable error) ;
  
  public MetricRegistry getMetricRegistry() ;
}