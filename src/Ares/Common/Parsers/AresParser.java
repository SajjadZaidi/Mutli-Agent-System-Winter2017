package Ares.Common.Parsers;

import Ares.Common.*;
import Ares.Common.Commands.*;
import Ares.Common.Commands.AgentCommands.*;
import Ares.Common.Commands.AresCommands.*;
import Ares.Common.World.*;
import Ares.Common.World.Info.*;
import java.io.*;
import java.util.*;

public class AresParser {

    public static final String delim = "[ \n\r\\(\\)\\[\\]\\{\\};,]+";

    public static Grid[][] buildWorld(String file_location) throws AresParserException {
        String line;
        Grid[][] world = null;
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(file_location.trim()))) {
                world = readWorldSize(in);
                while ((line = in.readLine()) != null) {
                    Grid grid = readAndBuildGrid(line);
                    if (grid != null) {
                        world[grid.getLocation().getRow()][grid.getLocation().getCol()] = grid;
                    }
                }
            }
        } catch (Exception e) {
            throw new AresParserException("Unable to read in startup world information file");
        }
        return world;
    }

    public static Grid[][] readWorldSize(BufferedReader in) throws Exception {
        String[] tokens = in.readLine().split(" ");
        int rows = Integer.parseInt(tokens[3]);
        int cols = Integer.parseInt(tokens[6]);
        return new Grid[rows][cols];
    }

    public static Grid readAndBuildGrid(String line) throws Exception {
        StringTokenizer tokens = new StringTokenizer(line, "[](),% ");
        int row = Integer.parseInt(tokens.nextToken());
        int col = Integer.parseInt(tokens.nextToken());
        String fire = tokens.nextToken();
        String kill = tokens.nextToken();
        String charge = tokens.nextToken();
        int percent_chance = Integer.parseInt(tokens.nextToken());
        Grid grid = new Grid(row, col);
        if (fire.charAt(0) == '+') {
            grid.setOnFire();
        } else {
            grid.setNotOnFire();
        }
        if (kill.charAt(0) == '+') {
            grid.setKiller();
        } else {
            grid.setStable();
        }
        if (charge.charAt(0) == '+') {
            grid.setChargingGrid();
        } else {
            grid.setNormalGrid();
        }
        grid.setPercentChance(percent_chance);
        return grid;
    }

    public static AresCommand parseAresCommand(String string) throws AresParserException {
        try {
            string = string.trim();
            StringTokenizer scanner = new StringTokenizer(string, delim, true);
            if (string.startsWith(Command.STR_CONNECT_OK)) {
                Text(scanner, Command.STR_CONNECT_OK);
                OpenRoundBracket(scanner);
                Text(scanner, "ID");
                int ID = Int(scanner);
                Comma(scanner);
                Text(scanner, "GID");
                int GID = Int(scanner);
                Comma(scanner);
                Text(scanner, "ENG_LEV");
                int energy_level = Int(scanner);
                Comma(scanner);
                Text(scanner, "LOC");
                OpenRoundBracket(scanner);
                Text(scanner, "ROW");
                int row = Int(scanner);
                Comma(scanner);
                Text(scanner, "COL");
                int col = Int(scanner);
                CloseRoundBracket(scanner);
                Comma(scanner);
                Text(scanner, "FILE");
                String world_filename = File(scanner);
                Done(scanner);
                return new CONNECT_OK(new AgentID(ID, GID), energy_level, new Location(row, col), world_filename);
            } else if (string.startsWith(Command.STR_DISCONNECT)) {
                Text(scanner, Command.STR_DISCONNECT);
                Done(scanner);
                return new DISCONNECT();
            } else if (string.startsWith(Command.STR_UNKNOWN)) {
                Text(scanner, Command.STR_UNKNOWN);
                Done(scanner);
                return new Ares.Common.Commands.AresCommands.ARES_UNKNOWN();
            } else if (string.startsWith(Command.STR_CMD_RESULT_END)) {
                Text(scanner, Command.STR_CMD_RESULT_END);
                Done(scanner);
                return new CMD_RESULT_END();
            } else if (string.startsWith(Command.STR_CMD_RESULT_START)) {
                Text(scanner, Command.STR_CMD_RESULT_START);
                OpenRoundBracket(scanner);
                int results = Int(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new CMD_RESULT_START(results);
            } else if (string.startsWith(Command.STR_DEATH_CARD)) {
                Text(scanner, Command.STR_DEATH_CARD);
                Done(scanner);
                return new DEATH_CARD();
            } else if (string.startsWith(Command.STR_FWD_MESSAGE)) {
                Text(scanner, Command.STR_FWD_MESSAGE);
                OpenRoundBracket(scanner);
                Text(scanner, "IDFrom");
                OpenRoundBracket(scanner);
                int ID = Int(scanner);
                Comma(scanner);
                int GID = Int(scanner);
                CloseRoundBracket(scanner);
                Comma(scanner);
                Text(scanner, "MsgSize");
                int MsgSize = Int(scanner);
                Comma(scanner);
                Text(scanner, "NUM_TO");
                int NUM_TO = Int(scanner);
                Comma(scanner);
                Text(scanner, "IDS");
                OpenRoundBracket(scanner);
                AgentIDList agent_id_list = IDList(NUM_TO, scanner);
                CloseRoundBracket(scanner);
                Comma(scanner);
                Text(scanner, "MSG");
                int MSGindex = string.indexOf("MSG") + 4;
                String message = string.substring(MSGindex, MSGindex + MsgSize);
                scanner = new StringTokenizer(string.substring(MSGindex + MsgSize + 1, string.length()), delim, true);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new FWD_MESSAGE(new AgentID(ID, GID), agent_id_list, message);
            } else if (string.startsWith(Command.STR_MESSAGES_END)) {
                Text(scanner, Command.STR_MESSAGES_END);
                Done(scanner);
                return new MESSAGES_END();
            } else if (string.startsWith(Command.STR_MESSAGES_START)) {
                Text(scanner, Command.STR_MESSAGES_START);
                OpenRoundBracket(scanner);
                int messages = Int(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new MESSAGES_START(messages);
            } else if (string.startsWith(Command.STR_MOVE_RESULT)) {
                Text(scanner, Command.STR_MOVE_RESULT);
                OpenRoundBracket(scanner);
                Text(scanner, "ENG_LEV");
                int energy_level = Int(scanner);
                Comma(scanner);
                SurroundInfo surround_info = SurroundInfo(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new MOVE_RESULT(energy_level, surround_info);
            } else if (string.startsWith(Command.STR_OBSERVE_RESULT)) {
                Text(scanner, Command.STR_OBSERVE_RESULT);
                OpenRoundBracket(scanner);
                Text(scanner, "ENG_LEV");
                int energy_level = Int(scanner);
                Comma(scanner);
                Text(scanner, "GRID_INFO");
                OpenRoundBracket(scanner);
                GridInfo top_layer_info = GridInfo(scanner);
                CloseRoundBracket(scanner);
                Comma(scanner);
                Text(scanner, "NUM_SIG");
                int NUM_SIG = Int(scanner);
                Comma(scanner);
                Text(scanner, "LIFE_SIG");
                OpenRoundBracket(scanner);
                LifeSignals life_signals = LifeSignals(NUM_SIG, scanner);
                CloseRoundBracket(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new OBSERVE_RESULT(energy_level, top_layer_info, life_signals);
            } else if (string.startsWith(Command.STR_ROUND_END)) {
                Text(scanner, Command.STR_ROUND_END);
                Done(scanner);
                return new ROUND_END();
            } else if (string.startsWith(Command.STR_ROUND_START)) {
                Text(scanner, Command.STR_ROUND_START);
                Done(scanner);
                return new ROUND_START();
            } else if (string.startsWith(Command.STR_SAVE_SURV_RESULT)) {
                Text(scanner, Command.STR_SAVE_SURV_RESULT);
                OpenRoundBracket(scanner);
                Text(scanner, "ENG_LEV");
                int energy_level = Int(scanner);
                Comma(scanner);
                SurroundInfo surround_info = SurroundInfo(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new SAVE_SURV_RESULT(energy_level, surround_info);
            } else if (string.startsWith(Command.STR_SLEEP_RESULT)) {
                Text(scanner, Command.STR_SLEEP_RESULT);
                OpenRoundBracket(scanner);
                Text(scanner, "RESULT");
                boolean success = Bool(scanner);
                Comma(scanner);
                Text(scanner, "CH_ENG");
                int charge_energy = Int(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new SLEEP_RESULT(success, charge_energy);
            } else if (string.startsWith(Command.STR_TEAM_DIG_RESULT)) {
                Text(scanner, Command.STR_TEAM_DIG_RESULT);
                OpenRoundBracket(scanner);
                Text(scanner, "ENG_LEV");
                int energy_level = Int(scanner);
                Comma(scanner);
                SurroundInfo surround_info = SurroundInfo(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new TEAM_DIG_RESULT(energy_level, surround_info);
            }
            throw new AresParserException("Cannot parse ARES Action from " + string);
        } catch (Exception ex) {
            return new ARES_UNKNOWN();
        }

    }

    public static AgentCommand parseAgentCommand(String string) throws AresParserException {
        try {
            string = string.trim();
            StringTokenizer scanner = new StringTokenizer(string, delim, true);
            if (string.startsWith(Command.STR_CONNECT)) {
                Text(scanner, Command.STR_CONNECT);
                OpenRoundBracket(scanner);
                String group_name = String(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new CONNECT(group_name);
            } else if (string.startsWith(Command.STR_END_TURN)) {
                Text(scanner, Command.STR_END_TURN);
                Done(scanner);
                return new END_TURN();
            } else if (string.startsWith(Command.STR_MOVE)) {
                Text(scanner, Command.STR_MOVE);
                OpenRoundBracket(scanner);
                Direction direction = Direction(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new MOVE(direction);
            } else if (string.startsWith(Command.STR_OBSERVE)) {
                Text(scanner, Command.STR_OBSERVE);
                OpenRoundBracket(scanner);
                Text(scanner, "ROW");
                int row = Int(scanner);
                Comma(scanner);
                Text(scanner, "COL");
                int col = Int(scanner);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new OBSERVE(new Location(row, col));
            } else if (string.startsWith(Command.STR_SAVE_SURV)) {
                Text(scanner, Command.STR_SAVE_SURV);
                Done(scanner);
                return new SAVE_SURV();
            } else if (string.startsWith(Command.STR_SEND_MESSAGE)) {
                Text(scanner, Command.STR_SEND_MESSAGE);
                OpenRoundBracket(scanner);
                Text(scanner, "NumTo");
                int NumTo = Int(scanner);
                Comma(scanner);
                Text(scanner, "MsgSize");
                int MsgSize = Int(scanner);
                Comma(scanner);
                Text(scanner, "ID_List");
                OpenRoundBracket(scanner);
                AgentIDList agent_id_list = IDList(NumTo, scanner);
                CloseRoundBracket(scanner);
                Comma(scanner);
                Text(scanner, "MSG");
                int MSGindex = string.indexOf("MSG") + 4;
                String message = string.substring(MSGindex, MSGindex + MsgSize);
                scanner = new StringTokenizer(string.substring(MSGindex + MsgSize + 1, string.length()), delim, true);
                CloseRoundBracket(scanner);
                Done(scanner);
                return new SEND_MESSAGE(agent_id_list, message);
            } else if (string.startsWith(Command.STR_SLEEP)) {
                Text(scanner, Command.STR_SLEEP);
                Done(scanner);
                return new SLEEP();
            } else if (string.startsWith(Command.STR_TEAM_DIG)) {
                Text(scanner, Command.STR_TEAM_DIG);
                Done(scanner);
                return new TEAM_DIG();
            }
            throw new AresParserException("Cannot parse Agent to Kernel Command from " + string);
        } catch (Exception e) {
            return new AGENT_UNKNOWN();
        }
    }

    public static String next(StringTokenizer scanner) {
        if (!scanner.hasMoreTokens()) {
            return null;
        }
        String s = scanner.nextToken();
        while (s.equals(" ")) {
            if (!scanner.hasMoreTokens()) {
                return null;
            }
            s = scanner.nextToken();
        }
        return s;
    }

    public static SurroundInfo SurroundInfo(StringTokenizer scanner) throws AresParserException {
        SurroundInfo Info = new SurroundInfo();
        Text(scanner, "CURR_GRID");
        OpenRoundBracket(scanner);
        Info.setCurrentInfo(GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, "NUM_SIG");
        int NUM_SIG = Int(scanner);
        Comma(scanner);
        Text(scanner, "LIFE_SIG");
        OpenRoundBracket(scanner);
        Info.setLifeSignals(LifeSignals(NUM_SIG, scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.NORTH_WEST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.NORTH_WEST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.NORTH.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.NORTH, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.NORTH_EAST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.NORTH_EAST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.EAST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.EAST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.SOUTH_EAST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.SOUTH_EAST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.SOUTH.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.SOUTH, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.SOUTH_WEST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.SOUTH_WEST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, Direction.WEST.toString());
        OpenRoundBracket(scanner);
        Info.setSurroundInfo(Direction.WEST, GridInfo(scanner));
        CloseRoundBracket(scanner);
        return Info;
    }

    public static GridInfo GridInfo(StringTokenizer scanner) throws AresParserException {
        String type = String(scanner);
        boolean normal_grid = true;
        switch (type) {
            case "NO_GRID":
                return new GridInfo();
            case "NORMAL_GRID":
                normal_grid = true;
                break;
            case "CHARGING_GRID":
                normal_grid = false;
                break;
            default:
                throw new AresParserException("Expected " + type + " to be <grid_type>");
        }
        OpenRoundBracket(scanner);
        Text(scanner, "ROW_ID");
        int row = Int(scanner);
        Comma(scanner);
        Text(scanner, "COL_ID");
        int col = Int(scanner);
        Comma(scanner);
        Text(scanner, "ON_FIRE");
        boolean on_fire = Bool(scanner);
        Comma(scanner);
        Text(scanner, "MV_COST");
        int move_cost = Int(scanner);
        Comma(scanner);
        Text(scanner, "NUM_AGT");
        int NUM_AGT = Int(scanner);
        Comma(scanner);
        Text(scanner, "ID_List");
        OpenRoundBracket(scanner);
        AgentIDList agent_id_list = IDList(NUM_AGT, scanner);
        CloseRoundBracket(scanner);
        Comma(scanner);
        Text(scanner, "TOP_LAYER");
        OpenRoundBracket(scanner);
        WorldObjectInfo top_layer_info = ObjectInfo(scanner);
        CloseRoundBracket(scanner);
        CloseRoundBracket(scanner);
        return new GridInfo(normal_grid, new Location(row, col), on_fire, move_cost, agent_id_list, top_layer_info);
    }

    public static WorldObjectInfo ObjectInfo(StringTokenizer scanner) throws AresParserException {
        String type = String(scanner);
        switch (type) {
            case "RUBBLE":
                return RubbleInfo(scanner);
            case "SURVIVOR":
                return SurvivorInfo(scanner);
            case "SURVIVOR_GROUP":
                return SurvivorGroupInfo(scanner);
            case "BOTTOM_LAYER":
                return new BottomLayerInfo();
            default:
                throw new AresParserException("Expected " + type + " to be <object>");
        }
    }

    public static WorldObjectInfo RubbleInfo(StringTokenizer scanner) throws AresParserException {
        OpenRoundBracket(scanner);
        Text(scanner, "ID");
        int ID = Int(scanner);
        Comma(scanner);
        Text(scanner, "NUM_TO_RM");
        int remove_agents = Int(scanner);
        Comma(scanner);
        Text(scanner, "RM_ENG");
        int remove_energy = Int(scanner);
        CloseRoundBracket(scanner);
        return new RubbleInfo(ID, remove_energy, remove_agents);
    }

    public static WorldObjectInfo SurvivorInfo(StringTokenizer scanner) throws AresParserException {
        OpenRoundBracket(scanner);
        Text(scanner, "ID");
        int ID = Int(scanner);
        Comma(scanner);
        Text(scanner, "ENG_LEV");
        int energy_level = Int(scanner);
        Comma(scanner);
        Text(scanner, "DMG_FAC");
        int damage_factor = Int(scanner);
        Comma(scanner);
        Text(scanner, "BDM");
        int body_mass = Int(scanner);
        Comma(scanner);
        Text(scanner, "MS");
        int mental_state = Int(scanner);
        CloseRoundBracket(scanner);
        return new SurvivorInfo(ID, energy_level, damage_factor, body_mass, mental_state);
    }

    public static WorldObjectInfo SurvivorGroupInfo(StringTokenizer scanner) throws AresParserException {
        OpenRoundBracket(scanner);
        Text(scanner, "ID");
        int ID = Int(scanner);
        Comma(scanner);
        Text(scanner, "NUM_SV");
        int number_of_survivors = Int(scanner);
        Comma(scanner);
        Text(scanner, "ENG_LV");
        int energy_level = Int(scanner);
        CloseRoundBracket(scanner);
        return new SurvivorGroupInfo(ID, energy_level, number_of_survivors);
    }

    public static AgentIDList IDList(int NUM_TO, StringTokenizer scanner) throws AresParserException {
        List<AgentID> idlist = new ArrayList<>(NUM_TO);
        for (int i = 0; i < NUM_TO; i++) {
            OpenSquareBracket(scanner);
            Text(scanner, "ID");
            int id = Int(scanner);
            Comma(scanner);
            Text(scanner, "GID");
            int gid = Int(scanner);
            CloseSquareBracket(scanner);
            idlist.add(new AgentID(id, gid));
            Comma(scanner);
        }
        return new AgentIDList(idlist);
    }

    public static LifeSignals LifeSignals(int NUM_SIG, StringTokenizer scanner) throws AresParserException {
        int[] signals = new int[NUM_SIG];
        for (int i = 0; i < NUM_SIG; i++) {
            int signal = Int(scanner);
            signals[i] = signal;
            Comma(scanner);
        }
        return new LifeSignals(signals);
    }

    public static Direction Direction(StringTokenizer scanner) throws AresParserException {
        return Direction.getDirection(String(scanner));
    }

    public static String File(StringTokenizer scanner) throws AresParserException {
        String FILE = "";
        String s;
        while (!(s = scanner.nextToken()).equals(")")) {
            FILE += s;
        }
        return FILE;
    }

    public static String String(StringTokenizer scanner) throws AresParserException {
        return next(scanner);
    }

    public static boolean Bool(StringTokenizer scanner) throws AresParserException {
        String s = next(scanner);
        try {
            return Boolean.valueOf(s);
        } catch (Exception e) {
            throw new AresParserException("Expected " + s + " to be <bool>");
        }
    }

    public static int Int(StringTokenizer scanner) throws AresParserException {
        String s = next(scanner);
        try {
            return new Integer(s);
        } catch (Exception e) {
            throw new AresParserException("Expected " + s + " to be <int>");
        }
    }

    public static void Text(StringTokenizer scanner, String text) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals(text)) {
            throw new AresParserException("Expected \"" + s + "\" to be \"" + text + "\"");
        }
    }

    public static void Comma(StringTokenizer scanner) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals(",")) {
            throw new AresParserException("Expected \"" + s + "\" to be \",\"");
        }
    }

    public static void OpenRoundBracket(StringTokenizer scanner) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals("(")) {
            throw new AresParserException("Expected \"" + s + "\" to be \")\"");
        }
    }

    public static void CloseRoundBracket(StringTokenizer scanner) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals(")")) {
            throw new AresParserException("Expected \"" + s + "\" to be \")\"");
        }
    }

    public static void OpenSquareBracket(StringTokenizer scanner) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals("[")) {
            throw new AresParserException("Expected \"" + s + "\" to be \"[\"");
        }
    }

    public static void CloseSquareBracket(StringTokenizer scanner) throws AresParserException {
        String s;
        if (!(s = next(scanner)).equals("]")) {
            throw new AresParserException("Expected \"" + s + "\" to be \"]\"");
        }
    }

    public static void Done(StringTokenizer scanner) throws AresParserException {
        if (next(scanner) != null) {
            throw new AresParserException("Expected to be done parsing");
        }
    }
}
