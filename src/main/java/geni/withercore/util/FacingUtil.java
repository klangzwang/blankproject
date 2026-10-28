package geni.withercore.util;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import geni.witherapi.RelativeSide;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FacingUtil {
	
    public static final Random rand;
    private static final Map<Direction, List<Direction>> s_perpendicularDirections;
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Direction[] POSITIVE_DIRECTIONS;
    public static final Direction[] NEGATIVE_DIRECTIONS;
    public static final RelativeSide[] SIDES = RelativeSide.values();
    
    static {
        rand = new Random();
        
        // perpendicular directions

        final List<Direction> xPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.UP });
        final List<Direction> yPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST });
        final List<Direction> zPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.EAST, Direction.DOWN, Direction.WEST, Direction.UP });

        s_perpendicularDirections = new Object2ObjectArrayMap<>(DIRECTIONS.length);
        s_perpendicularDirections.put(Direction.UP, yPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.DOWN, yPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.NORTH, zPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.SOUTH, zPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.EAST, xPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.WEST, xPerpendicularsDirections);
        
        POSITIVE_DIRECTIONS = Arrays.stream(DIRECTIONS).filter(d -> d.getAxisDirection() == Direction.AxisDirection.POSITIVE).toArray(Direction[]::new);
        NEGATIVE_DIRECTIONS = Arrays.stream(DIRECTIONS).filter(d -> d.getAxisDirection() == Direction.AxisDirection.NEGATIVE).toArray(Direction[]::new);
    }
    
    @SafeVarargs
    public static <E> E getRandomElement(final E... arr)
    {
        return arr[FacingUtil.rand.nextInt(arr.length)];
    }
    
    //region Direction helpers

    public static Stream<Direction> directionStream() {
        return Arrays.stream(DIRECTIONS);
    }

    public static Direction.Plane perpendicularPlane(final Direction direction) {
        return perpendicularPlane(direction.getAxis().getPlane());
    }

    public static Direction.Plane perpendicularPlane(final Direction.Axis axis) {
        return perpendicularPlane(axis.getPlane());
    }

    public static Direction.Plane perpendicularPlane(final Direction.Plane plane) {
        return Direction.Plane.HORIZONTAL == plane ? Direction.Plane.VERTICAL : Direction.Plane.HORIZONTAL;
    }

    public static List<Direction> perpendicularDirections(final Direction direction) {
        return s_perpendicularDirections.get(direction);
    }

    /**
     * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
     * returns this facing without rotating.
     */
    public static Direction directionRotateAround(final Direction direction, Direction.Axis axis) {

        switch (axis) {

            case X:

                if (direction != Direction.WEST && direction != Direction.EAST) {
                    return directionRotateX(direction);
                }

                return direction;

            case Y:

                if (direction != Direction.UP && direction != Direction.DOWN) {
                    return directionRotateY(direction);
                }

                return direction;

            case Z:

                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return directionRotateZ(direction);
                }

                return direction;

            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    /**
     * Rotate the given Direction around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public static Direction directionRotateY(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.EAST;

            case EAST:
                return Direction.SOUTH;

            case SOUTH:
                return Direction.WEST;

            case WEST:
                return Direction.NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + direction);
        }
    }

    /**
     * Rotate the given Direction around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public static Direction directionRotateX(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.DOWN;

            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + direction);

            case SOUTH:
                return Direction.UP;

            case UP:
                return Direction.NORTH;

            case DOWN:
                return Direction.SOUTH;
        }
    }

    /**
     * Rotate the given Direction around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    public static Direction directionRotateZ(final Direction direction) {

        switch (direction) {

            case EAST:
                return Direction.DOWN;

            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + direction);

            case WEST:
                return Direction.UP;

            case UP:
                return Direction.EAST;

            case DOWN:
                return Direction.WEST;
        }
    }

    /**
     * Rotate the given Direction around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public static Direction directionRotateYCCW(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.WEST;

            case EAST:
                return Direction.NORTH;

            case SOUTH:
                return Direction.EAST;

            case WEST:
                return Direction.SOUTH;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + direction);
        }
    }
    
    /*
     * 
     * FACING
     * 
     */
	public static final Direction[] VALUES = Direction.values();
	public static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES)
			.filter((direction) -> direction.getAxis().isHorizontal())
			.sorted(Comparator.comparingInt(Direction::get2DDataValue))
			.toArray(Direction[]::new);
	
    public static final BlockPos[] AROUND_X;
    public static final BlockPos[] AROUND_Y;
    public static final BlockPos[] AROUND_Z;
    
    public static final BlockPos[] AROUND_Y_ALL;
    public static final BlockPos[] AROUND_ALL;

    public static final Direction[] FACES_AROUND_X;
    public static final Direction[] FACES_AROUND_Y;
    public static final Direction[] FACES_AROUND_Z;

    static
    {
        AROUND_X = new BlockPos[]{new BlockPos(0, 1, -1), new BlockPos(0, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, -1, -1), new BlockPos(0, -1, 0), new BlockPos(0, -1, 1)};
        AROUND_Y = new BlockPos[]{new BlockPos(-1, 0, 1), new BlockPos(0, 0, 1), new BlockPos(1, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, -1), new BlockPos(0, 0, -1), new BlockPos(1, 0, -1)};
        AROUND_Z = new BlockPos[]{new BlockPos(1, 1, 0), new BlockPos(0, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, -1, 0), new BlockPos(0, -1, 0), new BlockPos(-1, -1, 0)};

        AROUND_Y_ALL = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(-1, 0, 1), new BlockPos(0, 0, 1), new BlockPos(1, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, -1), new BlockPos(0, 0, -1), new BlockPos(1, 0, -1)};

        List<BlockPos> list = new ArrayList<BlockPos>();

        for (BlockPos pos : AROUND_X)
        {
            if (!list.contains(pos))
            {
                list.add(pos);
            }
        }
        for (BlockPos pos : AROUND_Y)
        {
            if (!list.contains(pos))
            {
                list.add(pos);
            }
        }
        for (BlockPos pos : AROUND_Z)
        {
            if (!list.contains(pos))
            {
                list.add(pos);
            }
        }

        for (BlockPos pos : AROUND_Y_ALL)
        {
            if (!list.contains(pos))
            {
                list.add(pos);
            }
        }
        
        AROUND_ALL = list.toArray(new BlockPos[0]);

        List<Direction> x = new ArrayList<Direction>();
        List<Direction> y = new ArrayList<Direction>();
        List<Direction> z = new ArrayList<Direction>();

        for (Direction facing : Direction.values()) {
            if (facing.getAxis() != Direction.Axis.X) {
                x.add(facing);
            }
            if (facing.getAxis() != Direction.Axis.Y) {
                y.add(facing);
            }
            if (facing.getAxis() != Direction.Axis.Z) {
                z.add(facing);
            }
        }

        FACES_AROUND_X = x.toArray(new Direction[1]);
        FACES_AROUND_Y = y.toArray(new Direction[1]);
        FACES_AROUND_Z = z.toArray(new Direction[1]);
    }

    public static BlockPos[] getAroundAxis(Direction.Axis axis)
    {
        if (axis == Direction.Axis.X)
        {
            return AROUND_X;
        }
        else if (axis == Direction.Axis.Y)
        {
            return AROUND_Y;
        }
        else
        {
            return AROUND_Z;
        }
    }

    public static Direction[] getAxisFaces(Direction.Axis axis)
    {
        switch (axis)
        {
            case X: return new Direction[]{Direction.EAST, Direction.WEST};
            case Y: return new Direction[]{Direction.UP, DOWN};
            case Z: return new Direction[]{Direction.NORTH, Direction.SOUTH};
            default: return new Direction[0];
        }
    }

    public static Direction[] getFacingsAroundAxis(Direction.Axis axis)
    {
        if (axis == Direction.Axis.X)
        {
            return FACES_AROUND_X;
        }
        else if (axis == Direction.Axis.Y)
        {
            return FACES_AROUND_Y;
        }
        else
        {
            return FACES_AROUND_Z;
        }
    }

    public static Direction rotateAround(Direction facing, Direction.Axis axis, boolean reverse)
    {
        switch (axis) {
            case X:

                if (facing != Direction.WEST && facing != Direction.EAST) {
                    return rotateX(facing, reverse);
                }

                return facing;
            case Y:

                if (facing != Direction.UP && facing != DOWN) {
                    return rotateY(facing, reverse);
                }

                return facing;
            case Z:

                if (facing != Direction.NORTH && facing != Direction.SOUTH) {
                    return rotateZ(facing, reverse);
                }

                return facing;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    private static Direction rotateY(Direction facing, boolean reverse)
    {
        switch (facing) {
            case NORTH:
                return reverse ? Direction.WEST : Direction.EAST;
            case EAST:
                return reverse ? Direction.NORTH : Direction.SOUTH;
            case SOUTH:
                return reverse ? Direction.EAST : Direction.WEST;
            case WEST:
                return reverse ? Direction.SOUTH : Direction.NORTH;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + facing);
        }
    }

    private static Direction rotateX(Direction facing, boolean reverse)
    {
        switch (facing) {
            case NORTH:
                return reverse ? Direction.UP : DOWN;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + facing);
            case SOUTH:
                return reverse ? DOWN : Direction.UP;
            case UP:
                return reverse ? Direction.SOUTH : Direction.NORTH;
            case DOWN:
                return reverse ? Direction.NORTH : Direction.SOUTH;
        }
    }

    private static Direction rotateZ(Direction facing, boolean reverse)
    {
        switch (facing) {
            case EAST:
                return reverse ? Direction.UP : DOWN;
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + facing);
            case WEST:
                return reverse ? DOWN : Direction.UP;
            case UP:
                return reverse ? Direction.WEST : Direction.EAST;
            case DOWN:
                return reverse ? Direction.EAST : Direction.WEST;
        }
    }

    public static int distanceInDirection(BlockPos fromPos, BlockPos toPos, Direction direction)
    {
        switch (direction) {
            case DOWN:
                return fromPos.getY() - toPos.getY();
            case UP:
                return toPos.getY() - fromPos.getY();
            case NORTH:
                return fromPos.getZ() - toPos.getZ();
            case SOUTH:
                return toPos.getZ() - fromPos.getZ();
            case WEST:
                return fromPos.getX() - toPos.getX();
            case EAST:
                return toPos.getX() - fromPos.getX();
        }
        return 0;
    }

    public static Direction rotateXYZ(Direction current)
    {
        switch (current) {
            case DOWN:
                return UP;
            case UP:
                return NORTH;
            case NORTH:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return EAST;
            case EAST:
                return DOWN;
        }
        return NORTH;
    }

    /**
     * FUNKTION 1a: Konvertiert eine Achse (X, Y, Z) in eine spezifische Richtung.
     * @param axis Die Basis-Achse (z.B. aus der BlockState Property)
     * @param isPositive true für POSITIVE (UP, SOUTH, EAST), false für NEGATIVE (DOWN, NORTH, WEST)
     */
    public static Direction getDirectionFromAxis(Direction.Axis axis, boolean isPositive) {
        Direction.AxisDirection axisDir = isPositive ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        return Direction.fromAxisAndDirection(axis, axisDir);
    }

    /**
     * FUNKTION 1b: Gibt beide Richtungen zurück, die zu einer bestimmten Achse gehören.
     * Nützlich, wenn du z.B. bei der Y-Achse gleichzeitig den Block über und unter dir prüfen willst.
     */
    public static Direction[] getDirectionsForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            case Z -> new Direction[]{Direction.SOUTH, Direction.NORTH};
        };
    }
}
