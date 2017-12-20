package Ares.Common;

import java.util.*;

public class AgentIDList implements Iterable<AgentID> {

    private List<AgentID> agent_id_list;

    public AgentIDList() {
        agent_id_list = new ArrayList<AgentID>();
    }

    public AgentIDList(List<AgentID> agent_id_list) {
        this.agent_id_list = agent_id_list;
    }

    public void add(AgentID agent_id) {
        if (!agent_id_list.contains(agent_id)) {
            agent_id_list.add(agent_id);
        }
    }

    public void addAll(List<AgentID> agent_id_list) {
        for (AgentID agent_id : agent_id_list) {
            if (!this.agent_id_list.contains(agent_id)) {
                this.agent_id_list.add(agent_id);
            }
        }
    }

    public void remove(AgentID agent_id) {
        agent_id_list.remove(agent_id);
    }

    public void removeAll(List<AgentID> agent_id_list) {
        this.agent_id_list.removeAll(agent_id_list);
    }

    public int size() {
        return agent_id_list.size();
    }

    @Override
    public AgentIDList clone() {
        AgentIDList copy = new AgentIDList();
        for (AgentID agent : agent_id_list) {
            copy.add(agent.clone());
        }
        return copy;
    }

    @Override
    public String toString() {
        String s = "( ";
        for (AgentID agent_id : agent_id_list) {
            s += String.format("%s , ", agent_id);
        }
        return s + ")";
    }

    public String procString() {
        if (agent_id_list.isEmpty()) {
            return "all";
        }
        String s = "( ";
        for (AgentID agent_id : agent_id_list) {
            s += String.format("%s, ", agent_id.procString());
        }
        return s + ")";
    }

    @Override
    public Iterator<AgentID> iterator() {
        return agent_id_list.iterator();
    }

    public boolean isEmpty() {
        return agent_id_list.isEmpty();
    }

    public void clear() {
        agent_id_list.clear();
    }

    public AgentID remove(int i) {
        return agent_id_list.remove(i);
    }
}
