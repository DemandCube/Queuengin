package com.neverwinterdp.queuengin.kafka ;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MessageConsumerConnector;
import com.neverwinterdp.queuengin.MessageConsumerHandler;
import com.neverwinterdp.util.JSONSerializer;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class KafkaMessageConsumerConnector implements MessageConsumerConnector {
  private int numberOfThreads ;
  private ExecutorService executorService ;
  private  ConsumerConnector consumer;
  private Map<String, TopicMessageConsumers> topicConsumers = new ConcurrentHashMap<String, TopicMessageConsumers>() ;
  
  public KafkaMessageConsumerConnector(String group, String zkConnectUrls) {
    this(group, zkConnectUrls, 1) ;
  }
  
  public KafkaMessageConsumerConnector(String group, String zkConnectUrls, int numOfThreads) {
    this.numberOfThreads = numOfThreads ;
    
    Properties props = new Properties();
    props.put("group.id", group);
    props.put("zookeeper.connect", zkConnectUrls);
    props.put("zookeeper.session.timeout.ms", "400");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");
    //props.put("auto.commit.enable", "false");
    props.put("auto.offset.reset", "smallest");
    
    executorService = Executors.newFixedThreadPool(numOfThreads);
    ConsumerConfig config = new ConsumerConfig(props);
    consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
  }

  synchronized public void consume(String topic, MessageConsumerHandler handler, int numOfThreads) throws IOException {
    if(numOfThreads > this.numberOfThreads) numOfThreads = this.numberOfThreads ;
    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    topicCountMap.put(topic, numOfThreads);
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
    TopicMessageConsumer[] consumer = new TopicMessageConsumer[streams.size()] ;
    for (int i = 0; i < streams.size(); i++) {
      KafkaStream<byte[], byte[]> stream = streams.get(i) ;
      consumer[i] = new TopicMessageConsumer(handler, stream) ; 
      executorService.submit(consumer[i]);
    }
    
    TopicMessageConsumers topicConsumer = new TopicMessageConsumers(topic, consumer) ;
    topicConsumers.put(topic, topicConsumer) ;
  }
  
  synchronized public void remove(String topic) {
    TopicMessageConsumers topicConsumer = topicConsumers.get(topic) ;
    if(topicConsumer != null) {
      topicConsumers.remove(topic) ;
      topicConsumer.terminate(); 
    }
  }
  
  public void close() {
    executorService.shutdown() ;
    consumer.shutdown(); 
  }
  
  static public class TopicMessageConsumer implements Runnable {
    private MessageConsumerHandler handler ;
    private KafkaStream<byte[], byte[]> stream;
    private boolean terminate ;
    
    public TopicMessageConsumer(MessageConsumerHandler handler, KafkaStream<byte[], byte[]> stream) {
      this.handler = handler ;
      this.stream = stream;
    }

    public void setTerminate() {
      this.terminate = true ;
      
    }
    
    public void run() {
      ConsumerIterator<byte[], byte[]> it = stream.iterator();
      while (it.hasNext()) {
        if(terminate) return ;
        MessageAndMetadata<byte[], byte[]> data = it.next() ;
        byte[] key = data.key() ;
        byte[] mBytes = data.message() ;
        Message message = JSONSerializer.INSTANCE.fromBytes(mBytes, Message.class);
        handler.onMessage(message) ;
      }
    }
  }
  
  static public class TopicMessageConsumers {
    private String topic ;
    private TopicMessageConsumer[] consumers ;
    
    public TopicMessageConsumers(String topic, TopicMessageConsumer[] consumers) {
      this.topic = topic ;
      this.consumers = consumers ;
    }
    
    public String getTopic() { return this.topic ; }
    
    public void terminate() {
      for(TopicMessageConsumer sel : consumers) {
        sel.setTerminate();
      }
    }
  }
}