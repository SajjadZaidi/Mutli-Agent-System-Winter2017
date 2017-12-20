package Ares.Common.World.Objects;

import Ares.Common.World.Info.*;
import java.util.List;

public class Survivor extends WorldObject {

    protected int energy_level;
    protected int damage_factor;
    protected int body_mass;
    private int mental_state;

    public Survivor() {
        super();
        state = State.ALIVE;
        energy_level = 1;
        damage_factor = 0;
        body_mass = 0;
        mental_state = 0;
    }

    public Survivor(int energy_level, int damage_factor, int body_mass, int mental_state) {
        this();
        setEnergyLevel(energy_level);
        setDamageFactor(damage_factor);
        setBodyMass(body_mass);
        setMentalState(mental_state);
    }

    public Survivor(int ID, int energy_level, int damage_factor, int body_mass, int mental_state) {
        this(energy_level, damage_factor, body_mass, mental_state);
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

    public int getDamageFactor() {
        return damage_factor;
    }

    public final void setDamageFactor(int damage_factor) {
        this.damage_factor = damage_factor;
        setChanged();
        notifyObservers();
    }

    public int getBodyMass() {
        return body_mass;
    }

    public final void setBodyMass(int body_mass) {
        this.body_mass = body_mass;
        setChanged();
        notifyObservers();
    }

    public int getMentalState() {
        return mental_state;
    }

    public final void setMentalState(int mental_state) {
        this.mental_state = mental_state;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return String.format("SURVIVOR ( ID %s , ENG_LEV %s , DMG_FAC %s , BDM %s , MS %s )", ID, energy_level, damage_factor, body_mass, mental_state);
    }

    @Override
    public String getName(){
        return "Survivor";
    }

    @Override
    public int getLifeSignal() {
        int life_signal = energy_level;
        if (damage_factor > life_signal) {
            return 0;
        } else {
            life_signal -= damage_factor;
        }
        if (mental_state > life_signal) {
            return 0;
        } else {
            life_signal -= mental_state;
        }
        return life_signal;
    }

    @Override
    public WorldObjectInfo getObjectInfo() {
        return new SurvivorInfo(ID, energy_level, damage_factor, body_mass, mental_state);
    }

    @Override
    public String fileOutputString() {
        return String.format("SV(%s,%s,%s,%s)", energy_level, damage_factor, body_mass, mental_state);
    }

    @Override
    public List<String> stringInformation() {
        List<String> string_information = super.stringInformation();
        string_information.add(String.format("Energy Level = %s", energy_level));
        string_information.add(String.format("Damage Factor = %s", damage_factor));
        string_information.add(String.format("Body Mass = %s", body_mass));
        string_information.add(String.format("Mental State = %s", mental_state));
        return string_information;
    }

    @Override
    public Survivor clone() {
        Survivor survivor = (Survivor) super.clone();
        survivor.energy_level = energy_level;
        survivor.damage_factor = damage_factor;
        survivor.body_mass = body_mass;
        survivor.mental_state = mental_state;
        return survivor;
    }
}
