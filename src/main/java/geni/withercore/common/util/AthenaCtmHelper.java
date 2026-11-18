package geni.withercore.common.util;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

/**
 * Utility helper for Athena Connected Texture (CTM) calculation and 3D voxel preset generation.
 * Follows the 5-tile Athena CTM specification:
 * - 0: Center (Inner corner / concave notch: H && V && !D)
 * - 1: Empty (Full interior fill: H && V && D)
 * - 2: Horizontal (Horizontal edge: H && !V)
 * - 3: Vertical (Vertical edge: !H && V)
 * - 4: Particle (Outer corner / isolated: !H && !V)
 */
public class AthenaCtmHelper {

    public static final int ROLE_CENTER = 0;
    public static final int ROLE_EMPTY = 1;
    public static final int ROLE_HORIZONTAL = 2;
    public static final int ROLE_VERTICAL = 3;
    public static final int ROLE_PARTICLE = 4;

    public static final String[] ROLE_NAMES = {
            "Center (Inner Corner)",
            "Empty (Interior Fill)",
            "Horizontal Edge",
            "Vertical Edge",
            "Particle (Outer Corner)"
    };

    public enum ShapePreset {
        BOX("Box", "Solid Cuboid"),
        HOLLOW_FRAME("Frame", "Hollow Frame"),
        L_CORNER("L-Wall", "L-Corner Wall"),
        PLUS_CROSS("Cross", "Cross Structure"),
        STEPPED_BUNKER("Bunker", "Stepped Bunker"),
        CHECKER_PILLARS("Pillars", "Pillar Arcade");

        private final String label;
        private final String description;

        ShapePreset(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Resolves the Athena CTM Role Index (0..4) for a quadrant of a face.
     *
     * @param hasH whether horizontal neighbor exists on the face plane
     * @param hasV whether vertical neighbor exists on the face plane
     * @param hasD whether diagonal neighbor exists on the face plane
     * @return Athena role index (0..4)
     */
    public static int resolveQuadrantRole(boolean hasH, boolean hasV, boolean hasD) {
        if (!hasH && !hasV) return ROLE_PARTICLE;
        if (hasH && !hasV) return ROLE_HORIZONTAL;
        if (!hasH && hasV) return ROLE_VERTICAL;
        if (!hasD) return ROLE_CENTER;
        return ROLE_EMPTY;
    }

    /**
     * Basis coordinate system for one of the 6 cube faces.
     */
    public static class FaceBasis {
        public final Direction direction;
        public final int nx, ny, nz;
        public final int rx, ry, rz; // Tangent Right (+U on texture)
        public final int ux, uy, uz; // Tangent Up (+V on texture)

        public FaceBasis(Direction direction, int nx, int ny, int nz, int rx, int ry, int rz, int ux, int uy, int uz) {
            this.direction = direction;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.ux = ux;
            this.uy = uy;
            this.uz = uz;
        }
    }

    public static final FaceBasis[] FACES = new FaceBasis[] {
            // South (+Z): Right = +X, Up = +Y
            new FaceBasis(Direction.SOUTH, 0, 0, 1, 1, 0, 0, 0, 1, 0),
            // North (-Z): Right = -X, Up = +Y
            new FaceBasis(Direction.NORTH, 0, 0, -1, -1, 0, 0, 0, 1, 0),
            // East (+X): Right = -Z, Up = +Y
            new FaceBasis(Direction.EAST, 1, 0, 0, 0, 0, -1, 0, 1, 0),
            // West (-X): Right = +Z, Up = +Y
            new FaceBasis(Direction.WEST, -1, 0, 0, 0, 0, 1, 0, 1, 0),
            // Up (+Y): Right = +X, Up = -Z
            new FaceBasis(Direction.UP, 0, 1, 0, 1, 0, 0, 0, 0, -1),
            // Down (-Y): Right = +X, Up = +Z
            new FaceBasis(Direction.DOWN, 0, -1, 0, 1, 0, 0, 0, 0, 1)
    };

    /**
     * Builds a 3D boolean voxel matrix for given dimensions and shape preset.
     */
    public static boolean[][][] buildVoxelMatrix(int sizeX, int sizeY, int sizeZ, ShapePreset preset) {
        boolean[][][] matrix = new boolean[sizeX][sizeY][sizeZ];
        for (int y = 0; y < sizeY; y++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int x = 0; x < sizeX; x++) {
                    boolean active = false;
                    switch (preset) {
                        case BOX -> active = true;
                        case HOLLOW_FRAME -> {
                            boolean onXEdge = (x == 0 || x == sizeX - 1);
                            boolean onYEdge = (y == 0 || y == sizeY - 1);
                            boolean onZEdge = (z == 0 || z == sizeZ - 1);
                            int edgeCount = (onXEdge ? 1 : 0) + (onYEdge ? 1 : 0) + (onZEdge ? 1 : 0);
                            if (sizeZ == 1) {
                                active = (onXEdge || onYEdge);
                            } else {
                                active = (edgeCount >= 2);
                            }
                        }
                        case L_CORNER -> {
                            int thickness = Math.max(1, Math.min(sizeX, sizeY) / 3);
                            active = (x < thickness || y < thickness);
                        }
                        case PLUS_CROSS -> {
                            int midX = sizeX / 2;
                            int midY = sizeY / 2;
                            boolean bandX = (sizeX >= 5 ? Math.abs(x - midX) <= 1 : x == midX);
                            boolean bandY = (sizeY >= 5 ? Math.abs(y - midY) <= 1 : y == midY);
                            active = (bandX || bandY || (sizeX <= 2 && sizeY <= 2));
                        }
                        case STEPPED_BUNKER -> {
                            int distFromCorner = x + y + (sizeZ > 1 ? sizeZ - 1 - z : 0);
                            int maxStep = Math.max(1, sizeX + sizeY - 2);
                            active = distFromCorner <= Math.ceil(maxStep * 0.7);
                        }
                        case CHECKER_PILLARS -> {
                            active = (y == 0 || y == sizeY - 1 || (x % 2 == 0));
                        }
                    }
                    matrix[x][y][z] = active;
                }
            }
        }
        return matrix;
    }

    /**
     * Checks if a neighbor block at (x+dx, y+dy, z+dz) connects to block (x, y, z) on face normal (nx, ny, nz).
     * Includes Athena obscured-face culling check: if a block sits directly in front along the normal,
     * the connection terminates cleanly along the 3D step crease.
     */
    public static boolean isConnected(boolean[][][] matrix, int sx, int sy, int sz,
                                      int x, int y, int z, int dx, int dy, int dz,
                                      int nx, int ny, int nz, boolean respectObscuredCulling) {
        int tx = x + dx;
        int ty = y + dy;
        int tz = z + dz;

        if (tx < 0 || tx >= sx || ty < 0 || ty >= sy || tz < 0 || tz >= sz) {
            return false;
        }
        if (!matrix[tx][ty][tz]) {
            return false;
        }

        if (respectObscuredCulling) {
            int ox = tx + nx;
            int oy = ty + ny;
            int oz = tz + nz;
            if (ox >= 0 && ox < sx && oy >= 0 && oy < sy && oz >= 0 && oz < sz) {
                if (matrix[ox][oy][oz]) {
                    return false;
                }
            }
        }

        return true;
    }
}
