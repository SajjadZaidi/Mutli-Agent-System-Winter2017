package Ares.Common;

public class AgentID implements Comparable<AgentID> {

    private int ID;
    private int GID;

    public AgentID(int ID, int GID) {
        this.ID = ID;
        this.GID = GID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getGID() {
        return GID;
    }

    public void setGID(int GID) {
        this.GID = GID;
    }

    @Override
    public String toString() {
        return String.format("[ ID %s , GID %s ]", ID, GID);
    }

    public String procString() {
        return String.format("(%s,%s)", ID, GID);
    }

    @Override
    public AgentID clone() {
        return new AgentID(ID, GID);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AgentID) {
            AgentID agent_id = (AgentID) object;
            if (agent_id.ID == ID && agent_id.GID == GID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.ID;
        hash = 89 * hash + this.GID;
        return hash;
    }

    @Override
    public int compareTo(AgentID agent_id) {
        if (GID < agent_id.GID) {
            return -1;
        }
        if (GID > agent_id.GID) {
            return 1;
        }
        if (ID < agent_id.ID) {
            return -1;
        }
        if (ID > agent_id.ID) {
            return 1;
        }
        return 0;
    }
}
