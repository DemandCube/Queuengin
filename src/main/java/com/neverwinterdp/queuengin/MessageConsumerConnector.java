package com.neverwinterdp.queuengin ;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public interface MessageConsumerConnector {
  public void consume(String topic, MessageConsumerHandler handler, int numOfThreads) throws Exception ;
  public void close() ;
}