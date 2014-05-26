package com.neverwinterdp.queuengin.kinesis ;

import java.io.IOException;

import com.neverwinterdp.queuengin.MessageConsumerConnector;
import com.neverwinterdp.queuengin.MessageConsumerHandler;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KinesisMessageConsumerConnector implements MessageConsumerConnector {
  public void consume(String topic, MessageConsumerHandler handler, int numOfThreads) throws IOException {
  }

  public void close() {
  }
}