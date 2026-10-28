package geni.witherapi.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public interface ICustomBlockProperties {
    BlockBehaviour.Properties checkNeighbour(boolean enable);
    BlockBehaviour.Properties checkArea(boolean enable);
    boolean isCheckingNeighbour();
    boolean isCheckingArea();
}