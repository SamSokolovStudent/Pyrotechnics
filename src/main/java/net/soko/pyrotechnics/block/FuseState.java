package net.soko.pyrotechnics.block;

import net.minecraft.util.StringRepresentable;

public enum FuseState implements StringRepresentable {
    UNIGNITED("unignited"),
    IGNITED("ignited"),
    BURNT("burnt");
    private final String id;

    FuseState(String id) {
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
