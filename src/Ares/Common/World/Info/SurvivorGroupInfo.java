package Ares.Common.World.Info;

import Ares.Common.*;

public class SurvivorGroupInfo extends WorldObjectInfo {

    private int energy_level;
    private int number_of_survivors;

    public SurvivorGroupInfo(int ID, int energy_level, int number_of_survivors) {
        super(ID);
        this.energy_level = energy_level;
        this.number_of_survivors = number_of_survivors;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public void setEnergyLevel(int energy_level) {
        this.energy_level = energy_level;
    }

    public int getNumberOfSurvivors() {
        return number_of_survivors;
    }

    public void setNumberOfSurvivors(int number_of_survivors) {
        this.number_of_survivors = number_of_survivors;
    }

    @Override
    public String toString() {
        return String.format("SURVIVOR_GROUP ( ID %s , NUM_SV %s , ENG_LV %s )", ID, number_of_survivors, energy_level);
    }

    @Override
    public void distortInfo(int factor) {
        int distortion = Utility.randomInRange(1, factor);
        if (Utility.nextBoolean()) {
            if (distortion > energy_level) {
                energy_level = 0;
            } else {
                energy_level -= distortion;
            }
        } else {
            energy_level += distortion;
        }
    }
}
