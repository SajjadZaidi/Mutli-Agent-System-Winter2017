package Ares.Common.Commands.AresCommands;

import Ares.Common.*;
import Ares.Common.Commands.*;

public class CONNECT_OK extends AresCommand {

    private AgentID new_agent_id;
    private int energy_level;
    private Location location;
    private String world_filename;

    public CONNECT_OK(AgentID new_agent_id, int energy_level, Location location, String world_filename) {
        this.new_agent_id = new_agent_id;
        this.energy_level = energy_level;
        this.location = location;
        this.world_filename = world_filename;
    }

    public AgentID getNewAgentID() {
        return new_agent_id;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public Location getLocation() {
        return location;
    }

    public String getWorldFilename() {
        return world_filename;
    }

    @Override
    public String toString() {
        return String.format("%s ( ID %s , GID %s , ENG_LEV %s , LOC %s , FILE %s )", STR_CONNECT_OK, new_agent_id.getID(), new_agent_id.getGID(), energy_level, location, world_filename);
    }
}
