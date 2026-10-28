package geni.withercore.common.world.level.block.state;

import net.minecraft.util.StringRepresentable;

public enum MultiBlockState implements StringRepresentable {
    NONE("none"),
    XMIN_ZMIN_LO("xmin_zmin_lo"),
    XMIN_ZMAX_LO("xmin_zmax_lo"),
    XMAX_ZMIN_LO("xmax_zmin_lo"),
    XMAX_ZMAX_LO("xmax_zmax_lo"),
    XMIN_ZMIN_HI("xmin_zmin_hi"),
    XMIN_ZMAX_HI("xmin_zmax_hi"),
    XMAX_ZMIN_HI("xmax_zmin_hi"),
    XMAX_ZMAX_HI("xmax_zmax_hi"),
    XEDGE("xedge"),
    ZEDGE("zedge"),
    YEDGE("yedge"),
    XMIN_ZMIN_YEDGE("xmin_zmin_yedge"),
    XMIN_ZMAX_YEDGE("xmin_zmax_yedge"),
    XMAX_ZMIN_YEDGE("xmax_zmin_yedge"),
    XMAX_ZMAX_YEDGE("xmax_zmax_yedge"),
    CONTROLLER("controller"),
    XMIN_CENTER("xmin_center"),
    ZMIN_CENTER("zmin_center"),
    XMAX_CENTER("xmax_center"),
    ZMAX_CENTER("zmax_center"),
    YMIN_CENTER("ymin_center"),
    YMAX_CENTER("ymax_center");

    private final String name;

    MultiBlockState(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
