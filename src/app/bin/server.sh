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

JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m -XX:-UseSplitVerifier" 
APP_OPT="-Dapp.dir=$APP_DIR -Duser.dir=$APP_DIR"
LOG_OPT="-Dlog4j.configuration=file:$APP_DIR/config/log4j.properties"

MAIN_CLASS="com.neverwinterdp.server.Server"

function startServer {
  nohup $JAVACMD -Djava.ext.dirs=$APP_DIR/libs $APP_OPT $LOG_OPT $MAIN_CLASS "$@" <&- &>/dev/null &
  #printf '%d' $! > $SERVER_NAME.pid
}

function runServer {
  $JAVACMD -Djava.ext.dirs=$APP_DIR/libs $JAVA_OPTS $APP_OPT $LOG_OPT $MAIN_CLASS "$@"
}

echo "LOG_OPTS = $LOG_OPT"

#runServer -Pserver.name=generic -Pserver.roles=generic

startServer -Pserver.name=zookeeper -Pserver.roles=zookeeper
startServer -Pserver.name=kafka -Pserver.roles=kafka
