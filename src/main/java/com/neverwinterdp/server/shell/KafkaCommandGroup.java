package com.neverwinterdp.server.shell;

import com.neverwinterdp.server.gateway.Command;

@ShellCommandConfig(name = "kafka")
public class KafkaCommandGroup extends ShellCommand {
  public KafkaCommandGroup() {
    add("hello", HelloQueuenginCommand.class);
  }
  
  static public class HelloQueuenginCommand extends ShellSubCommand  {
    HelloQueuengin.Options options = new HelloQueuengin.Options();
    
    public void execute(Shell shell, ShellContext ctx, Command command) {
      try {
        command.mapAll(options);
        new HelloQueuengin().run(options);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}