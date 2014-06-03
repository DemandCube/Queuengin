
:echo "List the current server status"
:echo "==============================="
server ping

:echo "list the server and services registration"
:echo "=======================================--"
server registration

:echo "Install zookeeper on the zookeeper server role"
:echo "==============================================="
module install  \
  -Pmodule.data.drop=true \
  -Pzk:clientPort=2181 \
  --member-role zookeeper --autostart Zookeeper

:echo "Install kafka on the zookeeper server role"
:echo "========================================="
module install \
  -Pmodule.data.drop=true \
  -Pkafka:port=9092 -Pkafka:zookeeper.connect=127.0.0.1:2181 \
  -Pkafka.zookeeper-urls=127.0.0.1:2181 \
  -Pkafka.consumer-report.topics=Hello \
  --member-role kafka --autostart Kafka

:echo "list the server and services registration"
:echo "=======================================--"
server registration


:echo "Run kafka hello queuengin"
:echo "=======================================--"
kafka hello -topic Hello -num-message 50000

:echo "Uninstall Kafka server on the kafka role servers"
:echo "================================================"
module uninstall --member-role zookeeper --timeout 20000 Zookeeper

:echo "Uninstall Zookeeper server on the zookeeper role servers"
:echo "========================================================"
module uninstall --member-role kafka --timeout 20000 Kafka

:echo "list the server and services registration after uninstall kafka and zookeeper service"
:echo "====================================================================================="
server registration


:echo "shutdown the cluster"
:echo "===================="
server exit
