package Ares.Common.World.Objects;

import Ares.Common.World.Info.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class WorldObject extends Observable implements Cloneable {

    protected enum State {

        EXIST, ALIVE, DEAD;
    }
    protected State state;
    protected int ID;

    public WorldObject() {
        state = State.EXIST;
        ID = -1;
    }

    private State getState() {
        return state;
    }

    public boolean isExist() {
        return getState() == State.EXIST;
    }

    public boolean isAlive() {
        return getState() == State.ALIVE;
    }

    public boolean isDead() {
        return getState() == State.DEAD;
    }

    public void setExist() {
        setState(State.EXIST);
    }

    public void setAlive() {
        setState(State.ALIVE);
    }

    public void setDead() {
        setState(State.DEAD);
    }

    private void setState(State state) {
        this.state = state;
        setChanged();
        notifyObservers();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
        setChanged();
        notifyObservers();
    }

    @Override
    public abstract String toString();

    public abstract String getName();

    public abstract int getLifeSignal();

    public abstract WorldObjectInfo getObjectInfo();

    public abstract String fileOutputString();

    public List<String> stringInformation() {
        List<String> string_information = new ArrayList<>();
        string_information.add(String.format("State = %s", state));
        string_information.add(String.format("ID = %s", ID));
        return string_information;
    }

    @Override
    public WorldObject clone() {
        try {
            WorldObject world_object = (WorldObject) super.clone();
            world_object.state = state;
            world_object.ID = ID;
            return world_object;
        } catch (Exception e) {
            throw new InternalError(e.toString());
        }
    }
}
