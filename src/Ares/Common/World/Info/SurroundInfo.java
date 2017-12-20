package Ares.Common.World.Info;

import Ares.Common.*;

public class SurroundInfo {

    private LifeSignals life_signals = new LifeSignals();
    private GridInfo[][] surround_info = new GridInfo[3][3];

    public LifeSignals getLifeSignals() {
        return life_signals;
    }

    public void setLifeSignals(LifeSignals life_signals) {
        this.life_signals = life_signals;
    }

    public GridInfo getCurrentInfo() {
        return surround_info[Direction.STAY_PUT.getSurroundX()][Direction.STAY_PUT.getSurroundY()];
    }

    public void setCurrentInfo(GridInfo current_info) {
        surround_info[Direction.STAY_PUT.getSurroundX()][Direction.STAY_PUT.getSurroundY()] = current_info;
    }

    public GridInfo getSurroundInfo(Direction direction) {
        if (direction == Direction.UNKNOWN) {
            return null;
        }
        return surround_info[direction.getSurroundX()][direction.getSurroundY()];
    }

    public void setSurroundInfo(Direction direction, GridInfo grid_info) {
        if (direction != Direction.UNKNOWN) {
            surround_info[direction.getSurroundX()][direction.getSurroundY()] = grid_info;
        }
    }

    @Override
    public String toString() {
        return String.format("CURR_GRID ( %s ) , NUM_SIG %s , LIFE_SIG %s , NORTH_WEST ( %s ) , NORTH ( %s ) , NORTH_EAST ( %s ) , EAST ( %s ) , SOUTH_EAST ( %s ) , SOUTH ( %s ) , SOUTH_WEST ( %s ) , WEST ( %s )", getCurrentInfo(), life_signals.size(), life_signals, getSurroundInfo(Direction.NORTH_WEST), getSurroundInfo(Direction.NORTH), getSurroundInfo(Direction.NORTH_EAST), getSurroundInfo(Direction.EAST), getSurroundInfo(Direction.SOUTH_EAST), getSurroundInfo(Direction.SOUTH), getSurroundInfo(Direction.SOUTH_WEST), getSurroundInfo(Direction.WEST));
    }
}
