package Ares.Common.Commands;

import Ares.Common.*;

public abstract class AgentCommand extends Command {

    protected AgentID agent_id = new AgentID(-1, -1);

    public AgentID getAgentID() {
        return agent_id;
    }

    public void setAgentID(AgentID agent_id) {
        this.agent_id = agent_id;
    }

    public abstract String procString();
}
