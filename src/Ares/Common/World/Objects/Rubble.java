package Ares.Common.World.Objects;

import Ares.Common.World.Info.*;
import java.util.List;

public class Rubble extends WorldObject {

    private int remove_energy;
    private int remove_agents;

    public Rubble() {
        super();
        remove_energy = 1;
        remove_agents = 1;
    }

    public Rubble(int remove_energy, int remove_agents) {
        this();
        this.remove_energy = remove_energy;
        this.remove_agents = remove_agents;
    }

    public Rubble(int ID, int remove_energy, int remove_agents) {
        this(remove_energy, remove_agents);
        this.ID = ID;
    }

    public int getRemoveEnergy() {
        return remove_energy;
    }

    public void setRemoveEnergy(int remove_energy) {
        this.remove_energy = remove_energy;
        setChanged();
        notifyObservers();
    }

    public int getRemoveAgents() {
        return remove_agents;
    }

    public void setRemoveAgents(int remove_agents) {
        this.remove_agents = remove_agents;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return String.format("RUBBLE ( ID %s , NUM_TO_RM %s , RM_ENG %s )", ID, remove_agents, remove_energy);
    }

    @Override
    public String getName(){
        return "Rubble";
    }

    @Override
    public int getLifeSignal() {
        return 0;
    }

    @Override
    public WorldObjectInfo getObjectInfo() {
        return new RubbleInfo(ID, remove_energy, remove_agents);
    }

    @Override
    public String fileOutputString() {
        return String.format("RB(%s,%s)", remove_energy, remove_agents);
    }

    @Override
    public List<String> stringInformation() {
        List<String> string_information = super.stringInformation();
        string_information.add(String.format("Remove Energy = %s", remove_energy));
        string_information.add(String.format("Remove Agents = %s", remove_agents));
        return string_information;
    }

    @Override
    public WorldObject clone() {
        Rubble rubble = (Rubble) super.clone();
        rubble.remove_energy = remove_energy;
        rubble.remove_agents = remove_agents;
        return rubble;
    }
}
