package Ares.Common.World;

import Ares.Common.*;

public class World {

    private int rows;
    private int cols;
    private Grid[][] world;

    public World(Grid[][] world) {
        this.rows = world.length;
        this.cols = world[0].length;
        this.world = world;
    }

    public World(int rows, int cols) {
        if (rows > 0 && cols > 0) {
            this.rows = rows;
            this.cols = cols;
            world = new Grid[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    world[row][col] = new Grid(row, col);
                }
            }
        }
    }

    public Grid[][] getGrid() {
        return world;
    }

    public void setGrid(Grid[][] world) {
        this.rows = world.length;
        this.cols = world[0].length;
        this.world = world;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setGridAt(Location location, Grid grid) {
        if (world == null || location == null) {
            return;
        }
        if (location.valid(rows, cols)) {
            world[location.getRow()][location.getCol()] = grid;
        }
    }

    public Grid getGridAt(Location location) {
        if (world == null || location == null) {
            return null;
        }
        if (location.valid(rows, cols)) {
            return world[location.getRow()][location.getCol()];
        }
        return null;
    }

    public Grid getGridNeighbours(Direction direction, Location location) {
        return getGridAt(location.add(direction.getRowInc(), direction.getColInc()));
    }
}
