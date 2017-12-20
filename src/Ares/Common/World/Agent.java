package Ares.Common.World;

import Ares.Common.*;
import Ares.Common.World.Parts.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Agent extends Observable implements ObjectDisplay {

    private enum State {

        ALIVE, DEAD;
    }
    private AgentID agent_id;
    private Location location;
    private int energy_level;
    private State state;
    private Direction orientation;
    private String command_sent;

    public Agent(AgentID agent_id, Location location) {
        this.agent_id = agent_id;
        this.location = location;
        energy_level = Constants.DEFAULT_MAX_ENERGY_LEVEL;
        state = State.ALIVE;
        orientation = Direction.STAY_PUT;
        command_sent = "None";
    }

    public Agent(AgentID agent_id, Location location, int energy_level) {
        this(agent_id, location);
        this.energy_level = energy_level;
    }

    public AgentID getAgentID() {
        return agent_id;
    }

    public void setAgentID(AgentID agent_id) {
        this.agent_id = agent_id;
        setChanged();
        notifyObservers();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        setChanged();
        notifyObservers();
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public void setEnergyLevel(int energy_level) {
        this.energy_level = energy_level;
        if (energy_level <= 0) {
            state = State.DEAD;
        } else {
            state = State.ALIVE;
        }
        setChanged();
        notifyObservers();
    }

    public void addEnergy(int add_energy) {
        if (add_energy >= 0) {
            energy_level += add_energy;
        }
        setChanged();
        notifyObservers();
    }

    public void removeEnergy(int remove_energy) {
        if (remove_energy < energy_level) {
            energy_level -= remove_energy;
        } else {
            energy_level = 0;
            state = State.DEAD;
        }
        setChanged();
        notifyObservers();
    }

    private State getState() {
        return state;
    }

    public boolean isAlive() {
        return getState() == State.ALIVE;
    }

    public boolean isDead() {
        return getState() == State.DEAD;
    }

    public void setAlive() {
        setState(State.ALIVE);
    }

    public void setDead() {
        setState(State.DEAD);
    }

    private void setState(State state) {
        this.state = state;
        setChanged();
        notifyObservers();
    }

    public Direction getOrientation() {
        return orientation;
    }

    public void setOrientation(Direction orientation) {
        this.orientation = orientation;
        setChanged();
        notifyObservers();
    }

    public String getCommandSent() {
        return command_sent;
    }

    public void setCommandSent(String command_sent) {
        this.command_sent = command_sent;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return agent_id.toString();
    }

    public List<String> stringInformation() {
        List<String> string_information = new ArrayList<String>();
        string_information.add(String.format("AgentID       = %s", agent_id));
        string_information.add(String.format("Location      = %s", location));
        string_information.add(String.format("Energy Level  = %s", energy_level));
        string_information.add(String.format("State         = %s", state));
        string_information.add(String.format("Command Sent  = %s", command_sent));
        return string_information;
    }

    @Override
    public Agent clone() {
        Agent agent = new Agent(agent_id, location, energy_level);
        agent.state = state;
        agent.orientation = orientation;
        agent.command_sent = command_sent;
        return agent;
    }

    public void paintObject(Graphics graphics, int scale) {
        Color agent_color = Color.red;
        switch (agent_id.getGID()) {
            case 1:
                agent_color = Color.cyan;
                break;
            case 2:
                agent_color = Color.green;
                break;
            case 3:
                agent_color = Color.yellow;
                break;
            case 4:
                agent_color = Color.orange;
                break;
            case 5:
                agent_color = Color.red;
                break;
            case 6:
                agent_color = Color.pink;
                break;
            case 7:
                agent_color = Color.gray;
                break;
            default:
                agent_color = Color.red;
                break;
        }
        graphics.setColor(agent_color);
        graphics.fillOval(50, 5, scale, scale);
        graphics.setColor(Color.black);
        graphics.drawOval(50, 5, scale, scale);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Agent) {
            return agent_id.equals(((Agent) object).agent_id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return agent_id.hashCode();
    }

    public int compareTo(AgentID agent_id) {
        return agent_id.compareTo(agent_id);
    }
}
