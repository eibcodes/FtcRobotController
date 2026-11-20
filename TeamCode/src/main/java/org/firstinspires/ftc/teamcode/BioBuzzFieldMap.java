package org.firstinspires.ftc.teamcode;

public class BioBuzzFieldMap {

    public static final int GRID_SIZE = 144;       // 72x72 cells
    public static final double INCHES_PER_CELL = 1.0; // 144 in / 72 = 2.0 in/cell

    // Cell Type Constants
    public static final byte EMPTY             = 0;
    public static final byte HIVE_OBSTACLE     = 1;
    public static final byte FLOWER_OBSTACLE   = 2;
    public static final byte RED_GARDEN        = 3;
    public static final byte BLUE_GARDEN       = 4;
    public static final byte RED_LOADING_ZONE  = 5;
    public static final byte BLUE_LOADING_ZONE = 6;

    // Origin (0,0) = Audience-Left Corner (Tile A1)
    // row (y): 0 (Audience Wall) -> 71 (Scoring Wall)
    // col (x): 0 (Red Wall)      -> 71 (Blue Wall)
    public static byte[][] createFieldGrid() {
        byte[][] grid = new byte[GRID_SIZE][GRID_SIZE];

        // 1. Red Garden (Tile A1: 23" wide x 2" deep along Audience-Left corner)
        fillInches(grid, 0, 23, 0, 2, RED_GARDEN);

        // 2. Blue Garden (Tile F6: 23" wide x 2" deep along Scoring-Right corner)
        fillInches(grid, 121, 144, 142, 144, BLUE_GARDEN);

        // 3. Red Loading Zone (Tile A5: 23" wide x 11" deep, cols A, rows 96"-120")
        fillInches(grid, 0, 11, 97, 120, RED_LOADING_ZONE);

        // 4. Blue Loading Zone (Tile F2: 23" wide x 11" deep, cols F, rows 24"-48")
        fillInches(grid, 133, 144, 24, 47, BLUE_LOADING_ZONE);

        // 5. Central Hive Frame (49.46" wide x 38.95" deep centered at 72", 72")
        fillInches(grid, 72 - 24.73, 72 + 24.73, 72 - 19.48, 72 + 19.48, HIVE_OBSTACLE);

        // 6. Four Flowers (attached to perimeter walls, ~5" footprint into field)
        // Audience Wall Flowers (Tile seams B1/C1 at x=48" and D1/E1 at x=96")
        fillInches(grid, 45.5, 50.5, 0, 5.5, FLOWER_OBSTACLE);
        fillInches(grid, 93.5, 98.5, 0, 5.5, FLOWER_OBSTACLE);

        // Scoring Wall Flowers (Tile seams B6/C6 at x=48" and D6/E6 at x=96")
        fillInches(grid, 45.5, 50.5, 138.5, 144, FLOWER_OBSTACLE);
        fillInches(grid, 93.5, 98.5, 138.5, 144, FLOWER_OBSTACLE);

        return grid;
    }

    private static void fillInches(byte[][] grid, double xMin, double xMax,
                                   double yMin, double yMax, byte value) {
        int colStart = Math.max(0, (int) Math.floor(xMin / INCHES_PER_CELL));
        int colEnd   = Math.min(GRID_SIZE - 1, (int) Math.ceil(xMax / INCHES_PER_CELL) - 1);
        int rowStart = Math.max(0, (int) Math.floor(yMin / INCHES_PER_CELL));
        int rowEnd   = Math.min(GRID_SIZE - 1, (int) Math.ceil(yMax / INCHES_PER_CELL) - 1);

        for (int r = rowStart; r <= rowEnd; r++) {
            for (int c = colStart; c <= colEnd; c++) {
                grid[r][c] = value;
            }
        }
    }
}
