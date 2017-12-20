package Ares.Common.Commands.AresCommands;

import Ares.Common.*;
import Ares.Common.Commands.*;

public class FWD_MESSAGE extends AresCommand {

    private AgentID from_agent_id;
    private AgentIDList agent_id_list;
    private String message;
    private int number_left_to_read;

    public FWD_MESSAGE(AgentID from_agent_id, AgentIDList agent_id_list, String message) {
        this.from_agent_id = from_agent_id;
        this.agent_id_list = agent_id_list;
        this.message = message;
        number_left_to_read = agent_id_list.size();
    }

    public AgentID getFromAgentID() {
        return from_agent_id;
    }

    public AgentIDList getAgentIDList() {
        return agent_id_list;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s ( IDFrom ( %s , %s ) , MsgSize %s , NUM_TO %s , IDS %s , MSG %s )", STR_FWD_MESSAGE, from_agent_id.getID(), from_agent_id.getGID(), message.length(), agent_id_list.size(), agent_id_list, message);
    }

    public int getNumberLeftToRead() {
        return number_left_to_read;
    }

    public void setNumberLeftToRead(int number_left_to_read) {
        this.number_left_to_read = number_left_to_read;
    }

    public void decreaseNumberLeftToRead() {
        if (number_left_to_read <= 0) {
            return;
        }
        number_left_to_read--;
    }

    public void increaseNumberLeftToRead(int number_read_inc) {
        if (number_read_inc > 0) {
            number_left_to_read += number_read_inc;
        }
    }
}
