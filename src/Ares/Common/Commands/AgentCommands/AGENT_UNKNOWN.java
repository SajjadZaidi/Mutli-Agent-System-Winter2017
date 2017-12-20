package Ares.Common.Commands.AgentCommands;

import Ares.Common.Commands.*;

public class AGENT_UNKNOWN extends AgentCommand {

    @Override
    public String toString() {
        return STR_UNKNOWN;
    }

    @Override
    public String procString() {
        return String.format("%s#??", agent_id.procString());
    }
}
