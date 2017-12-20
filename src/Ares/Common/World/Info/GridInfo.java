package Ares.Common.World.Info;

import Ares.Common.*;

public class GridInfo {

    private enum Type {

        NO_GRID, NORMAL_GRID, CHARGING_GRID;
    }
    private Type type;
    private Location location;
    private Boolean on_fire;
    private int move_cost;
    private AgentIDList agent_id_list;
    private WorldObjectInfo top_layer_info;

    public GridInfo() {
        type = Type.NO_GRID;
        location = new Location(-1, -1);
        on_fire = false;
        move_cost = 0;
        agent_id_list = new AgentIDList();
        top_layer_info = new BottomLayerInfo();
    }

    public GridInfo(boolean normal_grid, Location location, boolean on_fire, int move_cost, AgentIDList agent_id_list, WorldObjectInfo top_layer_info) {
        if (normal_grid) {
            this.type = Type.NORMAL_GRID;
        } else {
            this.type = Type.CHARGING_GRID;
        }
        this.location = location;
        this.on_fire = on_fire;
        this.move_cost = move_cost;
        this.agent_id_list = agent_id_list;
        this.top_layer_info = top_layer_info;
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

    private void setType(Type grid_type) {
        this.type = grid_type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isOnFire() {
        return on_fire;
    }

    public void setOnFire() {
        this.on_fire = true;
    }

    public void setNotOnFire() {
        this.on_fire = false;
    }

    public int getMoveCost() {
        return move_cost;
    }

    public void setMoveCost(int move_cost) {
        this.move_cost = move_cost;
    }

    public AgentIDList getAgentIDList() {
        return agent_id_list;
    }

    public void setAgentIDList(AgentIDList agent_id_list) {
        this.agent_id_list = agent_id_list;
    }

    public WorldObjectInfo getTopLayerInfo() {
        return top_layer_info;
    }

    public void setTopLayerInfo(WorldObjectInfo top_layer_info) {
        this.top_layer_info = top_layer_info;
    }

    @Override
    public String toString() {
        if (type == Type.NO_GRID) {
            return type.toString();
        }
        return String.format("%s ( ROW_ID %s , COL_ID %s , ON_FIRE %s , MV_COST %s , NUM_AGT %s , ID_List %s , TOP_LAYER ( %s ) )", type.toString(), location.getRow(), location.getCol(), on_fire.toString().toUpperCase(), move_cost, agent_id_list.size(), agent_id_list, top_layer_info);

    }

    public void distortInfo(int factor) {
        top_layer_info.distortInfo(factor);
    }
}
