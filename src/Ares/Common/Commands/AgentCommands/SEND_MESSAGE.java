package Ares.Common.Commands.AgentCommands;

import Ares.Common.*;
import Ares.Common.Commands.*;

public class SEND_MESSAGE extends AgentCommand {

    private AgentIDList agent_id_list;
    private String message;

    public SEND_MESSAGE(AgentIDList agent_id_list, String message) {
        this.agent_id_list = agent_id_list;
        this.message = message;
    }

    public AgentIDList getAgentIDList() {
        return agent_id_list;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s ( NumTo %s , MsgSize %s , ID_List %s , MSG %s )", STR_SEND_MESSAGE, agent_id_list.size(), message.length(), agent_id_list, message);
    }

    @Override
    public String procString() {
        return String.format("%s#Send %s to %s", agent_id.procString(), message, agent_id_list.procString());
    }
}
