package Agent;

import Ares.Common.Commands.*;
import Ares.Common.Commands.AresCommands.*;
import Ares.Common.Parsers.*;
import Ares.Common.World.*;
import Ares.Common.World.Info.*;

public abstract class Brain {

    private World world;

    public Brain() {
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world_info) {
        this.world = world_info;
        BaseAgent.log(LogLevels.State_Changes, "Brain: New world");
    }

    public abstract void handleFwdMessage(FWD_MESSAGE msg);

    public abstract void handleMoveResult(MOVE_RESULT mr);

    public abstract void handleTeamDigResult(TEAM_DIG_RESULT tdr);

    public abstract void handleSaveSurvResult(SAVE_SURV_RESULT ssr);

    public abstract void handleObserveResult(OBSERVE_RESULT or);

    public abstract void handleDisconnect();

    public abstract void handleDead();

    public abstract void think();

    public void handleAresCommand(AresCommand ares_command) throws AresParserException {
        BaseAgent agent = BaseAgent.getBaseAgent();
        if (ares_command instanceof CONNECT_OK) {
            CONNECT_OK connect_ok = (CONNECT_OK) ares_command;
            agent.setId(connect_ok.getNewAgentID());
            agent.setEnergyLevel(connect_ok.getEnergyLevel());
            agent.setLocation(connect_ok.getLocation());
            world = new World(AresParser.buildWorld(connect_ok.getWorldFilename()));
            agent.setAgentState(AgentStates.CONNECTED);
            BaseAgent.log(LogLevels.Test, "Connected successfully");
            //for (int i = 0; i < world.length; i++) {
            //    for (int j = 0; j < world[i].length; j++) {
            //        BaseAgent.log(LogLevels.Test, "" + world[i][j]);
            //    }
            //}
        } else if (ares_command instanceof DEATH_CARD) {
            agent.setAgentState(AgentStates.SHUTTING_DOWN);
            handleDead();
            System.exit(0);
        } else if (ares_command instanceof DISCONNECT) {
            agent.setAgentState(AgentStates.SHUTTING_DOWN);
            handleDisconnect();
            System.exit(0);
        } else if (ares_command instanceof FWD_MESSAGE) {
            FWD_MESSAGE command = (FWD_MESSAGE) ares_command;
            handleFwdMessage(command);
        } else if (ares_command instanceof MESSAGES_END) {
            agent.setAgentState(AgentStates.IDLE);
        } else if (ares_command instanceof MESSAGES_START) {
            agent.setAgentState(AgentStates.READ_MAIL);
        } else if (ares_command instanceof MOVE_RESULT) {
            MOVE_RESULT command = (MOVE_RESULT) ares_command;
            GridInfo curr = command.getSurrroundInfo().getCurrentInfo();
            agent.setEnergyLevel(command.getEnergyLevel());
            agent.setLocation(curr.getLocation());
            handleMoveResult(command);
        } else if (ares_command instanceof OBSERVE_RESULT) {
            OBSERVE_RESULT command = (OBSERVE_RESULT) ares_command;
            agent.setEnergyLevel(command.getEnergyLevel());
            handleObserveResult(command);
        } else if (ares_command instanceof ROUND_END) {
            agent.setAgentState(AgentStates.IDLE);
        } else if (ares_command instanceof ROUND_START) {
            agent.setAgentState(AgentStates.THINK);
        } else if (ares_command instanceof SAVE_SURV_RESULT) {
            SAVE_SURV_RESULT command = (SAVE_SURV_RESULT) ares_command;
            GridInfo curr = command.getSurrroundInfo().getCurrentInfo();
            agent.setEnergyLevel(command.getEnergyLevel());
            agent.setLocation(curr.getLocation());
            handleSaveSurvResult(command);
        } else if (ares_command instanceof SLEEP_RESULT) {
            SLEEP_RESULT command = (SLEEP_RESULT) ares_command;
            if (command.wasSuccessful()) {
                agent.setEnergyLevel(command.getChargeEnergy());
            }
        } else if (ares_command instanceof TEAM_DIG_RESULT) {
            TEAM_DIG_RESULT command = (TEAM_DIG_RESULT) ares_command;
            GridInfo curr = command.getSurrroundInfo().getCurrentInfo();
            agent.setEnergyLevel(command.getEnergyLevel());
            agent.setLocation(curr.getLocation());
            handleTeamDigResult((TEAM_DIG_RESULT) ares_command);
        } else if (ares_command instanceof ARES_UNKNOWN) {
            BaseAgent.log(LogLevels.Always, "Brain: Got Unknown command reply from ARES");
        } else if (ares_command instanceof CMD_RESULT_START) {
        } else if (ares_command instanceof CMD_RESULT_END) {
        } else {
            BaseAgent.log(LogLevels.Always, "Brain: Got unrecognized reply from ARES " + ares_command.getClass());
        }
    }
}
