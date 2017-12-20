package Ares.Common.World.Info;

public abstract class WorldObjectInfo {

    protected int ID;
    
    public WorldObjectInfo(int ID){
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public abstract String toString();

    public abstract void distortInfo(int factor);
}
