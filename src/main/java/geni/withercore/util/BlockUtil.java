package geni.withercore.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
public class BlockUtil {

    public static boolean rotateBlockValidState(Level world, BlockPos pos, Direction side) {
        BlockState clicked = world.getBlockState(pos);
        if (clicked.getBlock() == null) {
            return false;
        }
        //    Block clickedBlock = clicked.getBlock();
        BlockState newState = null;
        if (clicked.is(BlockTags.SLABS)) {
            final String key = "type"; //top or bottom
            final String valueDupe = "double"; //actually theres 3 but dont worry about it
            //      clicked.get(property)
            for (Property<?> prop : clicked.getProperties()) {
                //yes
                if (prop.getName().equals(key)) {
                    //then cycle me
                    newState = clicked.cycle(prop); // cycle
                    if (newState.getValue(prop).toString().equals(valueDupe)) {
                        //haha just hack and skip. turns into length 2. dont worry about it
                        newState = newState.cycle(prop);
                    }
                }
            }
        }
        else if (clicked.hasProperty(RotatedPillarBlock.AXIS)) {
            //axis
            Axis current = clicked.getValue(RotatedPillarBlock.AXIS);
            switch (current) {
                case X:
                    newState = clicked.setValue(RotatedPillarBlock.AXIS, Axis.Y);
                    break;
                case Y:
                    newState = clicked.setValue(RotatedPillarBlock.AXIS, Axis.Z);
                    break;
                case Z:
                    newState = clicked.setValue(RotatedPillarBlock.AXIS, Axis.X);
                    break;
                default:
                    break;
                //
            }
            //clicked.rot
        }
        else {
            //default whatever
            switch (side) {
                case DOWN:
                    newState = clicked.rotate(world, pos, Rotation.CLOCKWISE_180);
                    break;
                case UP:
                    newState = clicked.rotate(world, pos, Rotation.CLOCKWISE_180);
                    break;
                case EAST:
                    newState = clicked.rotate(world, pos, Rotation.CLOCKWISE_90);
                    break;
                case NORTH:
                    newState = clicked.rotate(world, pos, Rotation.COUNTERCLOCKWISE_90);
                    break;
                case SOUTH:
                    newState = clicked.rotate(world, pos, Rotation.CLOCKWISE_90);
                    break;
                case WEST:
                    newState = clicked.rotate(world, pos, Rotation.COUNTERCLOCKWISE_90);
                    break;
                default:
                    break;
            }
        }
        boolean win = false;
        if (newState != null) {
            win = world.setBlockAndUpdate(pos, newState);
        }
        return win;
    }
}
