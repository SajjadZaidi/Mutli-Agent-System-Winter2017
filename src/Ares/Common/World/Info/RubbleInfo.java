package Ares.Common.World.Info;

import Ares.Common.*;

public class RubbleInfo extends WorldObjectInfo {

    private int remove_energy;
    private int remove_agents;

    public RubbleInfo(int ID, int remove_energy,int remove_agents) {
        super(ID);
        this.remove_energy = remove_energy;
        this.remove_agents = remove_agents;
    }

    public int getRemoveEnergy() {
        return remove_energy;
    }

    public void setRemoveEnergy(int remove_energy) {
        this.remove_energy = remove_energy;
    }

    public int getRemoveAgents() {
        return remove_agents;
    }

    public void setRemoveAgents(int remove_agents) {
        this.remove_agents = remove_agents;
    }

    @Override
    public void distortInfo(int factor) {
        int distortion = Utility.randomInRange(1, factor);
        if (Utility.nextBoolean()) {
            if (distortion > remove_agents) {
                remove_agents = 0;
            } else {
                remove_agents -= distortion;
            }
            if (distortion > remove_energy) {
                remove_energy = 0;
            } else {
                remove_energy -= distortion;
            }
        } else {
            remove_agents += distortion;
            remove_energy += distortion;
        }
    }

    @Override
    public String toString() {
        return String.format("RUBBLE ( ID %s , NUM_TO_RM %s , RM_ENG %s )", ID, remove_agents, remove_energy);
    }
}
