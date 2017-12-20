package Ares.Common;

public enum Direction {

    UNKNOWN(-1, -1, -1, 0, 0, "UNKNOWN", "Unknown"),
    NORTH_WEST(0, 0, 0, -1, -1, "NORTH_WEST", "North West"),
    NORTH(1, 0, 1, -1, 0, "NORTH", "North"),
    NORTH_EAST(2, 0, 2, -1, 1, "NORTH_EAST", "North East"),
    EAST(3, 1, 2, 0, 1, "EAST", "East"),
    SOUTH_EAST(4, 2, 2, 1, 1, "SOUTH_EAST", "South East"),
    SOUTH(5, 2, 1, 1, 0, "SOUTH", "South"),
    SOUTH_WEST(6, 2, 0, 1, -1, "SOUTH_WEST", "South West"),
    WEST(7, 1, 0, 0, -1, "WEST", "West"),
    STAY_PUT(8, 1, 1, 0, 0, "STAY_PUT", "Stay_Put");
    private int index;
    private int surround_x;
    private int surround_y;
    private int row_inc;
    private int col_inc;
    private String string;
    private String proc_string;

    Direction(int index, int surround_x, int surround_y, int row_inc, int col_inc, String string, String proc_string) {
        this.index = index;
        this.surround_x = surround_x;
        this.surround_y = surround_y;
        this.row_inc = row_inc;
        this.col_inc = col_inc;
        this.string = string;
        this.proc_string = proc_string;
    }

    public int getIndex() {
        return index;
    }

    public int getSurroundX() {
        return surround_x;
    }

    public int getSurroundY() {
        return surround_y;
    }

    public int getRowInc() {
        return row_inc;
    }

    public int getColInc() {
        return col_inc;
    }

    public static Direction getDirection(String string) {
        switch (string) {
            case "NORTH":
                return NORTH;
            case "SOUTH":
                return SOUTH;
            case "WEST":
                return WEST;
            case "EAST":
                return EAST;
            case "NORTH_WEST":
                return NORTH_WEST;
            case "NORTH_EAST":
                return NORTH_EAST;
            case "SOUTH_EAST":
                return SOUTH_EAST;
            case "SOUTH_WEST":
                return SOUTH_WEST;
            case "STAY_PUT":
                return STAY_PUT;
            default:
                return UNKNOWN;
        }
    }

    public static Direction getDirection(int index) {
        if (index == 0) {
            return NORTH_WEST;
        } else if (index == 1) {
            return NORTH;
        } else if (index == 2) {
            return NORTH_EAST;
        } else if (index == 3) {
            return EAST;
        } else if (index == 4) {
            return SOUTH_EAST;
        } else if (index == 5) {
            return SOUTH;
        } else if (index == 6) {
            return SOUTH_WEST;
        } else if (index == 7) {
            return WEST;
        } else if (index == 8) {
            return STAY_PUT;
        } else {
            return UNKNOWN;
        }
    }

    public static Direction getRandomDirection() {
        return getDirection(Utility.randomInRange(0, 8));
    }

    @Override
    public String toString() {
        return string;
    }

    public String getProcString() {
        return proc_string;
    }
}
