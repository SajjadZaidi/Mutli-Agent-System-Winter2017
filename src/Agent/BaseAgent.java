package Agent;

import Ares.Common.*;
import Ares.Common.Commands.*;
import Ares.Common.Commands.AgentCommands.*;
import Ares.Common.Network.*;
import Ares.Common.Parsers.*;

public class BaseAgent {

    public static final int AGENT_PORT = 6001;
    private AgentStates agent_state;
    private AgentID id;
    private Location location;
    private int energy_level;
    private Brain brain;
    private AresSocket ares_socket;
    private static BaseAgent agent;
    private static LogLevels log_level;
    private static boolean log_test;

    private BaseAgent() {
        agent_state = AgentStates.CONNECTING;
        id = new AgentID(-1, -1);
        location = new Location(-1, -1);
        energy_level = -1;
        brain = null;
        ares_socket = null;
        agent = null;
        log_level = LogLevels.None;
        log_test = false;
    }

    public static BaseAgent getBaseAgent() {
        if (agent == null) {
            agent = new BaseAgent();
        }
        return agent;
    }

    public void setAgentState(AgentStates agent_state) {
        log(LogLevels.State_Changes, agent_state.toString());
        synchronized (agent_state) {
            this.agent_state = agent_state;
        }
    }

    public AgentStates getAgentState() {
        synchronized (agent_state) {
            return agent_state;
        }
    }

    public AgentID getAgentID() {
        return id;
    }

    public void setId(AgentID id) {
        this.id = id;
        log(LogLevels.Always, "New ID " + this.id);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        log(LogLevels.Always, "New Location " + this.location);
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energy_level = energyLevel;
        log(LogLevels.Always, "New Energy " + this.energy_level);
    }

    public Brain getBrain() {
        return brain;
    }

    public void setBrain(Brain brain) {
        this.brain = brain;
        log(LogLevels.Always, "New Brain");
    }

    public void start(String host, String group_name, Brain brain) {
    	long start_time = System.currentTimeMillis();
    	try
    	{
	        if (agent_state == AgentStates.CONNECTING) {
	            this.brain = brain;
	            if (connectToARES(host, group_name)) {
	                runBaseAgentStates();
	            } else {
	                log(LogLevels.Error, "Failed to connect to ARES");
	            }
	        } else {
	            log(LogLevels.Error, "Multiple calls made to start method, ( call ignored )");
	        }
    	}
    	catch (Exception e)
    	{
    		long end = System.currentTimeMillis();
    		System.out.println("Time thinking (in start): " + ( end - start_time));
    		e.printStackTrace();
    	}
    }

    private boolean connectToARES(String host, String group_name) {
        boolean result = false;
        for (int connectTry = 1; connectTry <= 5; connectTry++) {
            log(LogLevels.Always, "Trying to connect to ARES ...");
            try {
                ares_socket = new AresSocket();
                ares_socket.connect(host, AGENT_PORT);
                ares_socket.sendMessage(new CONNECT(group_name).toString());
                brain.handleAresCommand(AresParser.parseAresCommand(ares_socket.readMessage()));
                if (getAgentState() == AgentStates.CONNECTED) {
                    result = true;
                }
            } catch (AresSocketException | AresParserException e) {
            }
            if (result) {
                break;
            } else {
                log(LogLevels.Always, "Failed to connect");
            }
        }
        if (result) {
            log(LogLevels.Always, "Connected");
        }
        return result;
    }

    private void runBaseAgentStates() throws Exception {
        boolean end = false;
        while (!end) {
            try {
                AresCommand ares_command;
                try {
                    ares_command = AresParser.parseAresCommand(ares_socket.readMessage());
                    if (ares_command != null) {
                        brain.handleAresCommand(ares_command);
                        switch (agent_state) {
                            case IDLE:
                            case READ_MAIL:
                            case GET_CMD_RESULT:
                                break;
                            case THINK:
                                brain.think();
                                break;
                            case SHUTTING_DOWN:
                                end = true;
                                break;
                        }
                    }
                } catch (AresParserException e) {
                	
                    log(LogLevels.Always, "Got AresParserException \"" + e.getMessage() + "\"");
                }
            } catch (AresSocketException e) {
                log(LogLevels.Always, "Got AresSocketException \"" + e.getMessage() + "\", shutting down.");
                e.printStackTrace();
                end = true;
                throw new Exception("aaaa");
            }
        }
        ares_socket.disconnect();
    }

    public void send(Command agent_action) {
        try {
            ares_socket.sendMessage(agent_action.toString());
            //log(LogLevels.Always, "Send " + agent_action.toString());
        } catch (AresSocketException ex) {
            log(LogLevels.Always, "Failed to send " + agent_action);
        }
    }

    public static LogLevels getLogLevel() {
        return BaseAgent.log_level;
    }

    public static void setLogLevel(LogLevels level) {
        BaseAgent.log_level = level;
    }

    public static boolean getLogTestInfo() {
        return BaseAgent.log_test;
    }

    public static void setLogTestInfo(boolean log_test) {
        BaseAgent.log_test = log_test;
    }

    public static void log(LogLevels lev, String message) {
        if (lev == LogLevels.Test) {
            if (log_test) {
                System.out.println("Agent: " + lev + " : " + message);
            }
        } else if (lev == LogLevels.Always) {
            System.out.println("Agent: " + message);
        } else if (lev == LogLevels.Warning) {
            System.out.println("Agent: WARNING " + message);
        } else if (lev == LogLevels.Error) {
            System.out.println("Agent: ERROR " + message);
        } else if (log_level != LogLevels.None || lev == LogLevels.All) {
            if (lev == log_level || log_level == LogLevels.All) {
                System.out.println("Agent: " + lev + " : " + message);
            }
        }
    }
}
