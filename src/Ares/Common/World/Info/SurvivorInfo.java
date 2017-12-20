package Ares.Common.World.Info;

import Ares.Common.*;

public class SurvivorInfo extends WorldObjectInfo {

    private int energy_level;
    private int damage_factor;
    private int body_mass;
    private int mental_state;

    public SurvivorInfo(int ID, int energy_level, int damage_factor, int body_mass, int mental_state) {
        super(ID);
        this.energy_level = energy_level;
        this.damage_factor = damage_factor;
        this.body_mass = body_mass;
        this.mental_state = mental_state;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public void setEnergyLevel(int energy_level) {
        this.energy_level = energy_level;
    }

    public int getDamageFactor() {
        return damage_factor;
    }

    public void setDamageFactor(int damage_factor) {
        this.damage_factor = damage_factor;
    }

    public int getBodyMass() {
        return body_mass;
    }

    public void setBodyMass(int body_mass) {
        this.body_mass = body_mass;
    }

    public int getMentalState() {
        return mental_state;
    }

    public void setMentalState(int mental_state) {
        this.mental_state = mental_state;
    }

    @Override
    public void distortInfo(int factor) {
        int distortion = Utility.randomInRange(1, factor);
        if (Utility.nextBoolean()) {
            energy_level += distortion;
            damage_factor += distortion;
            body_mass += distortion;
            mental_state += distortion;
        } else {
            if (distortion > this.energy_level) {
                energy_level = 0;
            } else {
                energy_level -= distortion;
            }
            if (distortion > this.damage_factor) {
                damage_factor = 0;
            } else {
                damage_factor -= distortion;
            }
            if (distortion > this.body_mass) {
                body_mass = 0;
            } else {
                body_mass -= distortion;
            }
            if (distortion > this.mental_state) {
                mental_state = 0;
            } else {
                mental_state -= distortion;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("SURVIVOR ( ID %s , ENG_LEV %s , DMG_FAC %s , BDM %s , MS %s )", ID, energy_level, damage_factor, body_mass, mental_state);
    }
}
