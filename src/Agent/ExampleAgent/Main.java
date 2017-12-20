package Agent.ExampleAgent;

import Agent.*;

public class Main {

    public static void main(String[] args) {
        if (args.length >= 2) {
            BaseAgent.setLogLevel(LogLevels.All);
            BaseAgent.getBaseAgent().start(args[0], args[1], new ExampleAgent());
        } else {
            System.out.println("Agent: usage java TestAgent <hostname> <groupname>");
        }
    }
}
