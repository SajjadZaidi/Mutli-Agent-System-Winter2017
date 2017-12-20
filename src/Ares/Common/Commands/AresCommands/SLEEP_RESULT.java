package Ares.Common.Commands.AresCommands;

import Ares.Common.Commands.*;

public class SLEEP_RESULT extends AresCommand {

    private Boolean success;
    private int charge_energy;
    
    public SLEEP_RESULT(boolean success, int charge_energy) {
        this.success = success;
        this.charge_energy = charge_energy;
    }

    public boolean wasSuccessful() {
        return this.success;
    }

    public int getChargeEnergy() {
        return charge_energy;
    }

    @Override
    public String toString() {
        return String.format("%s ( RESULT %s , CH_ENG %s )", STR_SLEEP_RESULT, success.toString().toUpperCase(), charge_energy);
    }
}
