package Ares.Common;

public class Location {

    private int row;
    private int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        return String.format("( ROW %s , COL %s )", row, col);
    }

    public String procString() {
        return String.format("(%s,%s)", row, col);
    }

    @Override
    public Location clone() {
        return new Location(row, col);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Location) {
            Location location = (Location) object;
            if (location.row == row && location.col == col) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.row;
        hash = 89 * hash + this.col;
        return hash;
    }

    public int compareTo(Location location) {
        if (row < location.row) {
            return -1;
        }
        if (row > location.row) {
            return 1;
        }
        if (col < location.col) {
            return -1;
        }
        if (col > location.col) {
            return 1;
        }
        return 0;
    }

    public boolean valid(int rows, int cols) {
        if (row < 0 || row >= rows) {
            return false;
        }
        if (col < 0 || col >= cols) {
            return false;
        }
        return true;
    }

    public Location add(int row, int col) {
        return new Location(this.row + row, this.col + col);
    }

    public double distance(Location location) {
        return Math.sqrt(Math.pow(this.row - location.row, 2.0) + Math.pow(this.col - location.col, 2.0));
    }
}
