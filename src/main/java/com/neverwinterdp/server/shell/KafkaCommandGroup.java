package com.neverwinterdp.server.shell;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

@CommandGroupConfig(name = "kafka")
public class KafkaCommandGroup extends CommandGroup {
  public KafkaCommandGroup() {
    add("hello", HelloQueuenginCommand.class);
  }
  
  @Parameters(commandDescription = "execute kafka hello command")
  static public class HelloQueuenginCommand extends Command {
    @ParametersDelegate
    HelloQueuengin.Options options = new HelloQueuengin.Options();
    
    public void execute(ShellContext ctx) {
      try {
        new HelloQueuengin().run(options);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}