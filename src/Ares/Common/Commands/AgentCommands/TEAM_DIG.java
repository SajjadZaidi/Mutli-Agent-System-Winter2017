package Ares.Common.Commands.AgentCommands;

import Ares.Common.Commands.*;

public class TEAM_DIG extends AgentCommand {

    @Override
    public String toString() {
        return STR_TEAM_DIG;
    }

    @Override
    public String procString() {
        return String.format("%s#Team Dig", agent_id.procString());
    }
}
