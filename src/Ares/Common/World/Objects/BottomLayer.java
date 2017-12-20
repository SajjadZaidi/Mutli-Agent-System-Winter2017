package Ares.Common.World.Objects;

import Ares.Common.World.Info.*;
import java.util.List;

public class BottomLayer extends WorldObject {

    public BottomLayer() {
        super();
    }

    @Override
    public String toString() {
        return "BOTTOM_LAYER";
    }

    @Override
    public String getName(){
        return "Bottom Layer";
    }

    @Override
    public int getLifeSignal() {
        return 0;
    }

    @Override
    public WorldObjectInfo getObjectInfo() {
        return new BottomLayerInfo();
    }

    @Override
    public String fileOutputString() {
        return "";
    }

    @Override
    public List<String> stringInformation() {
        List<String> string_information = super.stringInformation();
        return string_information;
    }

    @Override
    public BottomLayer clone() {
        BottomLayer bottom_layer = (BottomLayer) super.clone();
        return bottom_layer;
    }
}
