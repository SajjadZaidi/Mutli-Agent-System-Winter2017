package Ares.Common.Commands.AgentCommands;

import Ares.Common.Commands.*;

public class END_TURN extends AgentCommand {

    @Override
    public String toString() {
        return STR_END_TURN;
    }

    @Override
    public String procString() {
        return String.format("%s#End Turn", agent_id.procString());
    }
}
