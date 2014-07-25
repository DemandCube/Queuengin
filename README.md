#Queuengin#

Queuengin is a wrapper project to the Kafka and maybe later for the Kinesis project as well.

Queuengin contains:

1. The api and reusable code for the queue producer and consumer
2. ZookeeperClusterService is a wrapper service to the zookeeper server. Maybe this code should be moved to the NeverwinterDP-Commons later 
3. KafkaClusterService is a wrapper to the kafka server. The KafkaClusterService is implemented the cluster service api which mean the service can be installed , started, stopped by the cluster management tool.
4. KafkaMetricsConsumerService is a queue consumer service. All the messages are sent to the topic metrics.consumer(or custom topic name) will be dequeue, collect the statistic such the message size, number of retries, time to dequeue...

##ZookeeperClusterService##


To install, you can call this command from the cluster shell or the cluster gateway

```
module install 
  -Pmodule.data.drop=true
  -Pzk:clientPort=2181
  --member-role zookeeper --autostart --module Zookeeper
```
Where the parameter:
  

*  -P is the property name and value
*  -Pzk: are the zookeeper configuration properties, check https://zookeeper.apache.org  
*  --member-role or --member-name or --member-uuid to select the target servers
*  --autostart to launch the service automatically affer the installation
*  --module Zookeeper to select the zookeeper module to install


To uninstall, you can call this command from the cluster shell or the cluster gateway

```
module uninstall --member-role zookeeper --timeout 20000 --module Zookeeper
```

##KafkaClusterService##

To install, you can call this command from the cluster shell or the cluster gateway

```
 module install
   -Pmodule.data.drop=true 
   -Pkafka:port=9092
   -Pkafka:zookeeper.connect=127.0.0.1:2181
   --member-role kafka --autostart --module Kafka

```
Where the parameter:
  

*  -P is the property name and value
*  -Pkafka: are the kafka configuration properties, check http://kafka.apache.org/documentation.html 
*  --member-role or --member-name or --member-uuid to select the target servers
*  --autostart to launch the service automatically affer the installation
*  --module Kafka to select the zookeeper module to install


To uninstall

```
module uninstall --member-role kafka --timeout 20000 --module Kafka
```


#Build And Develop#

##Build With Gradle##

1. cd Queuengin
2. gradle clean build install

##Eclipse##

To generate the eclipse configuration

1. cd Queuengin
2. gradle eclipse

To import the project into the  eclipse

1. Choose File > Import
2. Choose General > Existing Projects into Workspace
3. Check Select root directory and browse to path/Queuengin
4. Select all the projects then click Finish
