package Ares.Common.Commands.AgentCommands;

import Ares.Common.*;
import Ares.Common.Commands.*;

public class OBSERVE extends AgentCommand {

    private Location location;

    public OBSERVE(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("%s %s", STR_OBSERVE, location);
    }

    @Override
    public String procString() {
        return String.format("%s#Observe %s", agent_id.procString(), location.procString());
    }
}
