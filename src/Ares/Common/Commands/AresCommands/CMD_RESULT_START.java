package Ares.Common.Commands.AresCommands;

import Ares.Common.Commands.*;

public class CMD_RESULT_START extends AresCommand {

    private int results;

    public CMD_RESULT_START(int results) {
        this.results = results;
    }

    public int getResults() {
        return results;
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", STR_CMD_RESULT_START, results);
    }
}
