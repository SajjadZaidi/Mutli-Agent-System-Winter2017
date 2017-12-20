package Ares.Common.World;

import Ares.Common.*;
import Ares.Common.World.Info.*;
import Ares.Common.World.Objects.*;
import Ares.Common.World.Parts.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Grid extends Observable implements ObjectDisplay {

    private enum Type {

        NO_GRID, NORMAL_GRID, CHARGING_GRID;
    }

    private enum State {

        STABLE_GRID, KILLER_GRID;
    }
    private Type type;
    private State state;
    protected Location location;
    private AgentIDList agent_id_list;
    private List<WorldObject> grid_layer_list;
    private boolean on_fire;
    private int move_cost;
    private Color move_cost_color;
    private int percent_chance;
    private LifeSignals stored_life_signals;
    public int round=0;
    public boolean was_visited = false;
    
    public Grid() {
        type = Type.NO_GRID;
        state = State.STABLE_GRID;
        location = new Location(-1, -1);
        on_fire = false;
        move_cost = 1;
        agent_id_list = new AgentIDList();
        grid_layer_list = new ArrayList<>();
        move_cost_color = Color.gray;
        percent_chance = -1;
        stored_life_signals = new LifeSignals();
    }

    public Grid(int row, int col) {
        this();
        location = new Location(row, col);
    }

    public void setupGrid(String grid_state_type) {
        grid_state_type = grid_state_type.toUpperCase().trim();
        if (grid_state_type.compareTo("NORMAL_GRIDS") == 0) {
            type = Type.NORMAL_GRID;
            on_fire = false;
            state = State.STABLE_GRID;
        } else if (grid_state_type.compareTo("CHARGING_GRIDS") == 0) {
            type = Type.CHARGING_GRID;
            on_fire = false;
            state = State.STABLE_GRID;
        } else if (grid_state_type.compareTo("FIRE_GRIDS") == 0) {
            type = Type.NORMAL_GRID;
            on_fire = true;
            state = State.KILLER_GRID;
        } else if (grid_state_type.compareTo("KILLER_GRIDS") == 0) {
            type = Type.NORMAL_GRID;
            on_fire = false;
            state = State.KILLER_GRID;
        }
        setChanged();
        notifyObservers();
    }

    private Type getType() {
        return type;
    }

    public boolean isNoGrid() {
        return getType() == Type.NO_GRID;
    }

    public boolean isNormalGrid() {
        return getType() == Type.NORMAL_GRID;
    }

    public boolean isChargingGrid() {
        return getType() == Type.CHARGING_GRID;
    }

    public void setNoGrid() {
        setType(Type.NO_GRID);
    }

    public void setNormalGrid() {
        setType(Type.NORMAL_GRID);
    }

    public void setChargingGrid() {
        setType(Type.CHARGING_GRID);
    }

    private void setType(Type type) {
        this.type = type;
        setChanged();
        notifyObservers();
    }

    private State getState() {
        return state;
    }

    public boolean isStable() {
        return getState() == State.STABLE_GRID;
    }

    public boolean isKiller() {
        return getState() == State.KILLER_GRID;
    }

    public void setStable() {
        setState(State.STABLE_GRID);
    }

    public void setKiller() {
        setState(State.KILLER_GRID);
    }

    private void setState(State state) {
        this.state = state;
        setChanged();
        notifyObservers();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        setChanged();
        notifyObservers();
    }

    public AgentIDList getAgentList() {
        return agent_id_list;
    }

    public void setAgentList(AgentIDList agent_id_list) {
        this.agent_id_list = agent_id_list;
        setChanged();
        notifyObservers();
    }

    public void addAgent(AgentID agent_id) {
        agent_id_list.add(agent_id);
        setChanged();
        notifyObservers();
    }

    public void removeAgent(AgentID agent_id) {
        agent_id_list.remove(agent_id);
        setChanged();
        notifyObservers();
    }

    public List<WorldObject> getGridLayers() {
        return grid_layer_list;
    }

    public void addLayer(WorldObject layer) {
        grid_layer_list.add(layer);
        setChanged();
        notifyObservers();
    }

    public WorldObject removeTopLayer() {
        if (grid_layer_list.isEmpty()) {
            return null;
        }
        WorldObject layer = grid_layer_list.remove(grid_layer_list.size() - 1);
        setChanged();
        notifyObservers();
        return layer;
    }

    public WorldObject getTopLayer() {
        if (grid_layer_list.isEmpty()) {
            return null;
        }
        return grid_layer_list.get(grid_layer_list.size() - 1);
    }

    public void setTopLayer(WorldObject top_layer) {
        grid_layer_list.clear();
        grid_layer_list.add(top_layer);
        setChanged();
        notifyObservers();
    }

    public int numberOfLayers() {
        return grid_layer_list.size();
    }

    public boolean isOnFire() {
        return on_fire;
    }

    public void setOnFire() {
        on_fire = true;
        setState(State.KILLER_GRID);
        setChanged();
        notifyObservers();
    }

    public void setNotOnFire() {
        on_fire = false;
        setState(State.STABLE_GRID);
        setChanged();
        notifyObservers();
    }

    public void setOnFire(boolean on_fire) {
        if (on_fire) {
            setOnFire();
        } else {
            setNotOnFire();
        }
    }

    public int getMoveCost() {
        return move_cost;
    }

    public void setMoveCost(int move_cost) {
        this.move_cost = move_cost;
        setChanged();
        notifyObservers();
    }

    public Color getMoveCostColor() {
        return move_cost_color;
    }

    public void setMoveCostColor(Color move_cost_color) {
        this.move_cost_color = move_cost_color;
        setChanged();
        notifyObservers();
    }

    public int getPercentChange() {
        return percent_chance;
    }

    public void setPercentChance(int percent_chance) {
        this.percent_chance = percent_chance;
    }

    public GridInfo getGridInfo() {
        return new GridInfo(type == Type.NORMAL_GRID, location.clone(), on_fire, move_cost, agent_id_list.clone(), topLayerInfo());
    }

    public void setStoredLifeSignals(LifeSignals stored_life_signals) {
        this.stored_life_signals = stored_life_signals;
    }

    public LifeSignals getStoredLifeSignals() {
        return stored_life_signals;
    }

    private WorldObjectInfo topLayerInfo() {
        if (!grid_layer_list.isEmpty()) {
            return getTopLayer().getObjectInfo();
        } else {
            return new BottomLayerInfo();
        }
    }

    public int numberOfSurvivors() {
        int count = 0;
        for (WorldObject layer : grid_layer_list) {
            if (layer instanceof Survivor) {
                count++;
            }
            if (layer instanceof SurvivorGroup) {
                count += ((SurvivorGroup) layer).getNumberOfSurvivors();
            }
        }
        return count;
    }

    public LifeSignals getGeneratedLifeSignals() {
        int layer = grid_layer_list.size() - 1;
        int distortion;
        int lss;
        int i = 0;
        if (grid_layer_list.isEmpty()) {
            return new LifeSignals();
        }
        int[] life_signals = new int[grid_layer_list.size()];
        life_signals[i] = grid_layer_list.get(layer).getLifeSignal();
        i++;
        layer--;
        //2013 Distoration (original)
        int low_range = Constants.DEPTH_LOW_START;
        int high_range = Constants.DEPTH_HIGH_START;
        while (layer >= 0) {
            lss = grid_layer_list.get(layer).getLifeSignal();
            distortion = Utility.randomInRange(low_range, high_range);
            if (distortion > lss) {
                lss = 0;
            } else {
                lss -= distortion;
            }
            life_signals[i] = lss;
            i++;
            layer--;
            low_range += Constants.DEPTH_LOW_INC;
            high_range += Constants.DEPTH_HIGH_INC;
        }
        /*
         * Winter 2012 Distoration
         int low_range = 0;
         int high_range = 5;
         while (layer >= 0) {
         lss = grid_layer_list.get(layer).getLifeSignal();
         distortion = Utility.randomInRange(low_range, high_range);
         if (distortion > lss) {
         lss = 0;
         } else {
         lss -= distortion;
         }
         life_signals[i] = lss;
         i++;
         layer--;
         low_range += 1;
         high_range += 2;
         }
         */
        return new LifeSignals(life_signals);
    }

    public String fileOutputString() {
        String s = "Grid ( (" + location.getRow() + "," + location.getCol() + "), Move_Cost " + move_cost + ") \n\t\t{\n";
        for (WorldObject layer : grid_layer_list) {
            s += "\t\t    " + layer.fileOutputString() + ";\n";
        }
        s += "\t\t}\n\n";
        return s;
    }

    @Override
    public Grid clone() {
        Grid grid = new Grid();
        grid.type = type;
        grid.state = state;
        grid.location = location.clone();
        grid.agent_id_list = agent_id_list.clone();
        grid.grid_layer_list = new ArrayList<>();
        for (WorldObject layer : grid_layer_list) {
            grid.grid_layer_list.add(layer.clone());
        }
        grid.on_fire = on_fire;
        grid.move_cost = move_cost;
        grid.move_cost_color = move_cost_color;
        grid.percent_chance = percent_chance;
        return grid;
    }

    public void paint(Graphics graphics, int scale) {
        paintGridAt(location.getCol(), location.getRow(), graphics, scale);
    }

    @Override
    public void paintObject(Graphics graphics, int scale) {
        graphics.setColor(Color.green.darker());
        graphics.fillRect(0, 0, scale, scale);
        paintGridAt(0, 0, graphics, scale);
        graphics.setColor(Color.white);
        graphics.drawRect(0, 0, scale, scale);
    }

    public void paintGridAt(int x, int y, Graphics graphics, int scale) {
        Graphics2D buffer2d;
        if (isOnFire()) {
            graphics.setColor(Color.red.darker());
            graphics.fillRect(x * scale, y * scale, scale, scale);
        } else if (state == State.KILLER_GRID) {
            graphics.setColor(Color.yellow.darker());
            graphics.fillRect(x * scale, y * scale, scale, scale);
        } else {
            switch (getType()) {
                case NORMAL_GRID:
                    break;
                case CHARGING_GRID:
                    graphics.setColor(Color.blue.darker());
                    graphics.fillRect(x * scale, y * scale, scale, scale);
                    break;
            }
            Font font = null;
            Font font2 = new Font(null, Font.BOLD, scale / 6);
            int draw_agent_count = 5;
            if (draw_agent_count < agent_id_list.size()) {
                draw_agent_count = agent_id_list.size();
            }
            if (agent_id_list.size() > 0) {
                font = new Font(null, Font.BOLD, scale / 6);
            }
            int i = 0;
            for (AgentID agent : agent_id_list) {
                Color agent_color;
                switch (agent.getGID()) {
                    case 1:
                        agent_color = Color.cyan;
                        break;
                    case 2:
                        agent_color = Color.green;
                        break;
                    case 3:
                        agent_color = Color.yellow;
                        break;
                    case 4:
                        agent_color = Color.orange;
                        break;
                    case 5:
                        agent_color = Color.red;
                        break;
                    case 6:
                        agent_color = Color.pink;
                        break;
                    case 7:
                        agent_color = Color.gray;
                        break;
                    default:
                        agent_color = Color.red;
                        break;
                }
                graphics.setColor(agent_color);
                graphics.fillOval((scale * x) + ((scale / draw_agent_count) * i), scale * y, scale / draw_agent_count, scale / draw_agent_count);
                graphics.setColor(Color.black);
                graphics.drawOval((scale * x) + ((scale / draw_agent_count) * i), scale * y, scale / draw_agent_count, scale / draw_agent_count);
                graphics.drawOval((scale * x) + ((scale / draw_agent_count) * i), scale * y, scale / draw_agent_count, scale / draw_agent_count);
                graphics.setColor(Color.black);
                graphics.setFont(font);
                graphics.drawString("" + agent.getID(), (scale * x) + ((scale / draw_agent_count) * i) + (scale / (draw_agent_count * 4)), (scale * y) + (scale / draw_agent_count) - (scale / (draw_agent_count * 4)));
                i++;
            }
            WorldObject top_layer = getTopLayer();
            buffer2d = (Graphics2D) graphics;
            if (top_layer != null) {
                Color object_color = null;
                if (top_layer instanceof Rubble) {
                    object_color = Color.darkGray;
                } else if (top_layer instanceof Survivor) {
                    object_color = Color.cyan.darker();
                } else if (top_layer instanceof SurvivorGroup) {
                    object_color = Color.blue;
                }
                buffer2d.setColor(object_color);
                buffer2d.fillRect(x * scale, (y * scale) + (scale - (scale / 3)), scale / 3, scale / 3);
                buffer2d.setColor(Color.black);
                buffer2d.drawRect(x * scale, (y * scale) + (scale - (scale / 3)), scale / 3, scale / 3);
                if (top_layer instanceof Rubble) {
                    Rubble rubble = (Rubble) top_layer;
                    graphics.setColor(Color.white);
                    graphics.setFont(font2);
                    graphics.drawString("" + rubble.getRemoveAgents(), (x * scale) + scale / 6, (y * scale) + (scale - (scale / 6)));
                }
            }
            buffer2d.setColor(move_cost_color);
            buffer2d.fillRect((x * scale) + (scale - (scale / 6)), (y * scale) + (scale - (scale / 6)), scale / 6, scale / 6);
            buffer2d.setColor(Color.black);
            buffer2d.drawRect((x * scale) + (scale - (scale / 6)), (y * scale) + (scale - (scale / 6)), scale / 6, scale / 6);
        }
    }
}
