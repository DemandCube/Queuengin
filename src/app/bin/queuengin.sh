#!/bin/bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
APP_DIR=`cd $bin/..; pwd; cd $bin`
JAVACMD=$JAVA_HOME/bin/java

if $cygwin; then
  APP_DIR=`cygpath --absolute --windows "$APP_DIR"`
fi

function standaloneQueuengin {
  echo "Launching the zookeeper server in the deamon mode"
  $APP_DIR/bin/zookeeper.sh deamon && sleep 2
  echo "Launching the kafka server in the deamon mode"
  $APP_DIR/bin/kafka.sh     deamon
}

function killQueuengin {
  echo "Killing the kafka deamon"
  $APP_DIR/bin/kafka.sh     kill
  echo "Killing the zookeeper deamon"
  $APP_DIR/bin/zookeeper.sh kill
}

function helloQueuengin() {
  $JAVACMD -Djava.ext.dirs=$APP_DIR/libs com.neverwinterdp.queuengin.HelloQueuengin "$@"
}


COMMAND=$1
shift

if [ "$COMMAND" = "standalone" ] ; then
  standaloneQueuengin
elif [ "$COMMAND" = "kill" ] ; then
  killQueuengin
elif [ "$COMMAND" = "hello" ] ; then
  helloQueuengin "$@"
elif [ "$COMMAND" = "quick-test" ] ; then
  echo "**************************************************************************"
  echo "This quick test will:"
  echo "  1. Launch the zookeeper server in the deamon mode"
  echo "  2. Launch the kafka server in the deamon mode"
  echo "  3. Run the hello queuengin producer and consumer"
  echo "  4. Kill zookeeper and kafka server once the hello job terminate"
  echo "**************************************************************************"
  standaloneQueuengin 
  echo "Waiting 15s to make sure that the zookeeper and kafka server are launched."
  echo "If your hello test has some problem with connection, probably your computer is a bit slow."
  echo "You need to increase the wait time"
  sleep 15
  helloQueuengin -num-message 100000
  killQueuengin
else
  echo "Avaliable Commands: "
  echo "  standalone: Launch zookeeper and kafka in the deamon mode"
  echo "  kill:       Kill zookeeper and kafka deamon"
  echo "  hello:      Run hello producer and consumer"
  echo "  quick-test: Launch zookeeper, kafka server. Run hello and then shutdown"
fi
