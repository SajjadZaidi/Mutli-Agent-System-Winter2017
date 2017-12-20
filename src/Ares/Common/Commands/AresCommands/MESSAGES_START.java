package Ares.Common.Commands.AresCommands;

import Ares.Common.Commands.*;

public class MESSAGES_START extends AresCommand {

    private int messages;

    public MESSAGES_START(int messages) {
        this.messages = messages;
    }

    public int getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", STR_MESSAGES_START, messages);
    }
}
