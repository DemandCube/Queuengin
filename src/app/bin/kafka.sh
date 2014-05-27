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

PID_FILE="$APP_DIR/bin/kafka.pid"
APP_OPT="-Dapp.dir=$APP_DIR -Duser.dir=$APP_DIR"
LOG_OPT="-Dlog4j.configuration=file:$APP_DIR/config/kafka/log4j.properties"

KAFKA_MAIN_CLASS="com.neverwinterdp.queuengin.kafka.cluster.KafkaServer"

function runConsole {
  $JAVACMD -Djava.ext.dirs=$APP_DIR/libs $APP_OPT $LOG_OPT $KAFKA_MAIN_CLASS "$@"
}

function runDeamon {
  nohup $JAVACMD -Djava.ext.dirs=$APP_DIR/libs $APP_OPT $LOG_OPT $KAFKA_MAIN_CLASS "$@" <&- &>/dev/null &
  printf '%d' $! > $PID_FILE
}

function killDeamon {
   kill -9 `cat $PID_FILE` && rm -rf $PID_FILE
}


COMMAND=$1
shift

if [ "$COMMAND" = "run" ] ; then
  runConsole "$@"
elif [ "$COMMAND" = "deamon" ] ; then
  runDeamon "$@"
elif [ "$COMMAND" = "kill" ] ; then
  killDeamon "$@"
else
  echo "Avaliable Commands: "
  echo "  run:    Run kafka in the console mode"
  echo "  deamon: Run kafka in the deamon mode"
  echo "  kill:   Kill kafka deamon"
fi
