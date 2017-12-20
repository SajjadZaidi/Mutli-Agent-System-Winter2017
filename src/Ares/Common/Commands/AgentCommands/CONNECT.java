package Ares.Common.Commands.AgentCommands;

import Ares.Common.Commands.*;

public class CONNECT extends AgentCommand {

    private String group_name;

    public CONNECT(String group_name) {
        this.group_name = group_name;
    }

    public String getGroupName() {
        return group_name;
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", STR_CONNECT, group_name);
    }

    @Override
    public String procString() {
        return String.format("%s#Connect", agent_id.procString());
    }
}
