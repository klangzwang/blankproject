package geni.withercore.common.world.level.block.state;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockStateProperties {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final EnumProperty<MultiBlockState> MBSTATE = EnumProperty.create("mbstate", MultiBlockState.class);
}
