package org.firstinspires.ftc.teamcode;

import java.util.Arrays;

public class ZeroAllocAStar {

    /*
    • Instantiate once during init(). Calling new ZeroAllocAStar(fieldGrid, 10) builds the inflated obstacle map before the match starts so your autonomous loop never pays that setup cost.
    • Choose the right inflation radius. For an 18×18 inch robot, 9 inches is the exact half-width, while 10 or 11 inches adds a 1–2 inch safety buffer for odometry drift. If you drive holonomically (mecanum) while rotating, use your robot's half-diagonal (~12.7 → 13 inches) so corners stay clear when spinning.
    • Downsample waypoints before feeding a spline follower. A 144×144 A* path produces waypoints every 1 inch. If you pass these to Road Runner or Pedro Pathing, keep only the start, end, and points where the direction changes (or sample every 6–8 inches) so your trajectory generator builds smooth curves instead of micro-segments.
     */

    public static final int GRID_SIZE = 144;
    public static final int TOTAL_CELLS = GRID_SIZE * GRID_SIZE; // 20,736

    // Fixed-point scaling (1000 = 1.0 inch) to avoid floating-point overhead
    private static final int COST_STRAIGHT = 1000;
    private static final int COST_DIAGONAL = 1414;

    // 8-direction neighbor offsets (N, S, E, W, NE, NW, SE, SW)
    private static final int[] DR = {-1,  1,  0,  0, -1, -1,  1,  1};
    private static final int[] DC = { 0,  0,  1, -1,  1, -1,  1, -1};
    private static final int[] MOVE_COST = {
            COST_STRAIGHT, COST_STRAIGHT, COST_STRAIGHT, COST_STRAIGHT,
            COST_DIAGONAL, COST_DIAGONAL, COST_DIAGONAL, COST_DIAGONAL
    };

    // Pre-allocated workspace arrays (allocated ONCE in constructor)
    private final boolean[] blocked = new boolean[TOTAL_CELLS];
    private final int[] gCost = new int[TOTAL_CELLS];
    private final int[] parent = new int[TOTAL_CELLS];
    private final boolean[] closed = new boolean[TOTAL_CELLS];

    // Primitive Binary Min-Heap for Open Set
    private final int[] heapNodes = new int[TOTAL_CELLS];
    private final int[] heapFCost = new int[TOTAL_CELLS];
    private int heapSize = 0;

    // Reusable output buffers for reconstructed path (row, col pairs)
    public final int[] pathRows = new int[TOTAL_CELLS];
    public final int[] pathCols = new int[TOTAL_CELLS];
    public int pathLength = 0;

    /**
     * Builds the navigable C-space map once during OpMode init().
     *
     * @param rawGrid           144x144 grid from BioBuzzFieldMap
     * @param robotRadiusInches Half of robot's diagonal or max width + safety margin (e.g., 10 or 11 inches)
     */
    public ZeroAllocAStar(byte[][] rawGrid, int robotRadiusInches) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                // Treat Hive (1), Flowers (2), and perimeter walls as impassable
                boolean isWall = (r < robotRadiusInches || r >= GRID_SIZE - robotRadiusInches ||
                        c < robotRadiusInches || c >= GRID_SIZE - robotRadiusInches);
                boolean isObstacle = (rawGrid[r][c] == BioBuzzFieldMap.HIVE_OBSTACLE ||
                        rawGrid[r][c] == BioBuzzFieldMap.FLOWER_OBSTACLE);

                if (isWall) {
                    blocked[r * GRID_SIZE + c] = true;
                }
                if (isObstacle) {
                    inflateObstacle(r, c, robotRadiusInches);
                }
            }
        }
    }

    private void inflateObstacle(int centerR, int centerC, int radius) {
        int rSq = radius * radius;
        int minR = Math.max(0, centerR - radius);
        int maxR = Math.min(GRID_SIZE - 1, centerR + radius);
        int minC = Math.max(0, centerC - radius);
        int maxC = Math.min(GRID_SIZE - 1, centerC + radius);

        for (int r = minR; r <= maxR; r++) {
            int dr = r - centerR;
            for (int c = minC; c <= maxC; c++) {
                int dc = c - centerC;
                if (dr * dr + dc * dc <= rSq) {
                    blocked[r * GRID_SIZE + c] = true;
                }
            }
        }
    }

    /**
     * Computes shortest path from (startRow, startCol) to (goalRow, goalCol).
     * Allocates ZERO objects. Results are stored in pathRows[] and pathCols[].
     *
     * @return true if a valid path was found, false otherwise
     */
    public boolean findPath(int startRow, int startCol, int goalRow, int goalCol) {
        pathLength = 0;
        if (!isValid(startRow, startCol) || !isValid(goalRow, goalCol)) return false;

        int startIdx = startRow * GRID_SIZE + startCol;
        int goalIdx = goalRow * GRID_SIZE + goalCol;
        if (blocked[startIdx] || blocked[goalIdx]) return false;

        // Reset reusable arrays (fast native memory fill, zero allocation)
        Arrays.fill(gCost, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        Arrays.fill(closed, false);
        heapSize = 0;

        gCost[startIdx] = 0;
        pushHeap(startIdx, octileHeuristic(startRow, startCol, goalRow, goalCol));

        while (heapSize > 0) {
            int current = popHeap();
            if (current == goalIdx) {
                reconstructPath(goalIdx);
                return true;
            }

            if (closed[current]) continue;
            closed[current] = true;

            int curR = current / GRID_SIZE;
            int curC = current % GRID_SIZE;
            int curG = gCost[current];

            for (int i = 0; i < 8; i++) {
                int nextR = curR + DR[i];
                int nextC = curC + DC[i];
                if (nextR < 0 || nextR >= GRID_SIZE || nextC < 0 || nextC >= GRID_SIZE) continue;

                int nextIdx = nextR * GRID_SIZE + nextC;
                if (blocked[nextIdx] || closed[nextIdx]) continue;

                // Prevent corner-cutting through diagonal obstacles
                if (i >= 4) {
                    if (blocked[curR * GRID_SIZE + nextC] || blocked[nextR * GRID_SIZE + curC]) {
                        continue;
                    }
                }

                int tentativeG = curG + MOVE_COST[i];
                if (tentativeG < gCost[nextIdx]) {
                    gCost[nextIdx] = tentativeG;
                    parent[nextIdx] = current;
                    int fCost = tentativeG + octileHeuristic(nextR, nextC, goalRow, goalCol);
                    pushHeap(nextIdx, fCost);
                }
            }
        }
        return false;
    }

    private void reconstructPath(int goalIdx) {
        int count = 0;
        int curr = goalIdx;
        while (curr != -1) {
            pathRows[count] = curr / GRID_SIZE;
            pathCols[count] = curr % GRID_SIZE;
            count++;
            curr = parent[curr];
        }
        // Reverse in-place so index 0 is start and index (pathLength - 1) is goal
        for (int i = 0; i < count / 2; i++) {
            int tmpR = pathRows[i];
            int tmpC = pathCols[i];
            pathRows[i] = pathRows[count - 1 - i];
            pathCols[i] = pathCols[count - 1 - i];
            pathRows[count - 1 - i] = tmpR;
            pathCols[count - 1 - i] = tmpC;
        }
        pathLength = count;
    }

    private int octileHeuristic(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);
        int min = Math.min(dr, dc);
        int max = Math.max(dr, dc);
        return COST_DIAGONAL * min + COST_STRAIGHT * (max - min);
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE;
    }

    private void pushHeap(int node, int fCost) {
        int i = heapSize++;
        while (i > 0) {
            int p = (i - 1) >>> 1;
            if (heapFCost[p] <= fCost) break;
            heapNodes[i] = heapNodes[p];
            heapFCost[i] = heapFCost[p];
            i = p;
        }
        heapNodes[i] = node;
        heapFCost[i] = fCost;
    }

    private int popHeap() {
        int topNode = heapNodes[0];
        int lastNode = heapNodes[--heapSize];
        int lastF = heapFCost[heapSize];

        int i = 0;
        int half = heapSize >>> 1;
        while (i < half) {
            int child = (i << 1) + 1;
            int right = child + 1;
            if (right < heapSize && heapFCost[right] < heapFCost[child]) {
                child = right;
            }
            if (lastF <= heapFCost[child]) break;
            heapNodes[i] = heapNodes[child];
            heapFCost[i] = heapFCost[child];
            i = child;
        }
        heapNodes[i] = lastNode;
        heapFCost[i] = lastF;
        return topNode;
    }
}
