package Ares.Common.World.Objects;

import Ares.Common.World.Info.*;
import java.util.List;

public class SurvivorGroup extends WorldObject {

    private int energy_level;
    private int number_of_survivors;

    public SurvivorGroup() {
        super();
        state = State.ALIVE;
        energy_level = 1;
        number_of_survivors = 1;
    }

    public SurvivorGroup(int energy_level, int number_of_survivors) {
        this();
        this.setEnergyLevel(energy_level);
        this.setNumberOfSurvivors(number_of_survivors);
    }

    public SurvivorGroup(int ID, int energy_level, int number_of_survivors) {
        this(energy_level, number_of_survivors);
        this.ID = ID;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public final void setEnergyLevel(int energy_level) {
        this.energy_level = energy_level;
        if (energy_level <= 0) {
            setDead();
        } else {
            setAlive();
        }
        setChanged();
        notifyObservers();
    }

    public void removeEnergy(int remove_energy) {
        if (remove_energy < energy_level) {
            energy_level -= remove_energy;
        } else {
            energy_level = 0;
            setDead();
        }
        setChanged();
        notifyObservers();
    }

    public int getNumberOfSurvivors() {
        return number_of_survivors;
    }

    public final void setNumberOfSurvivors(int number_of_survivors) {
        this.number_of_survivors = number_of_survivors;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return String.format("SURVIVOR_GROUP ( ID %s , NUM_SV %s , ENG_LV %s )", ID, number_of_survivors, energy_level);
    }

    public String getName(){
        return "Survivor Group";
    }

    public int getLifeSignal() {
        return energy_level;
    }

    public WorldObjectInfo getObjectInfo() {
        return new SurvivorGroupInfo(ID, energy_level, number_of_survivors);
    }

    @Override
    public String fileOutputString() {
        return String.format("SVG(%s,%s)", energy_level, number_of_survivors);
    }

    @Override
    public List<String> stringInformation() {
        List<String> string_information = super.stringInformation();
        string_information.add(String.format("Energy Level = %s", energy_level));
        string_information.add(String.format("Number of SV = %s", number_of_survivors));
        return string_information;
    }

    @Override
    public SurvivorGroup clone() {
        SurvivorGroup survivor_group = (SurvivorGroup) super.clone();
        survivor_group.energy_level = energy_level;
        survivor_group.number_of_survivors = number_of_survivors;
        return survivor_group;
    }
}
