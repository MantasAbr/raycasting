package raycasting;

import levels.WallSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Grid traversal plus thin partitions, shared by raycasting and movement. */
public final class WorldGeometry {
    private final int[][] map;
    private final List<WallSegment>[][] wallsByTile;
    private final boolean hasWalls;

    public static final class Hit {
        public double distance, x, y;
        public int tileX, tileY, side, textureId;
        public boolean thinWall;
    }

    public WorldGeometry(int[][] map) {
        this(map, Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public WorldGeometry(int[][] map, List<WallSegment> walls) {
        this.map = map;
        hasWalls = !walls.isEmpty();
        wallsByTile = hasWalls ? (List<WallSegment>[][]) new List[map.length][] : null;
        if (!hasWalls)
            return;
        for (int x = 0; x < map.length; x++)
            wallsByTile[x] = (List<WallSegment>[]) new List[map[x].length];
        for (WallSegment wall : walls) {
            for (int x = Math.max(0, (int) Math.floor(wall.minX)); x <= Math.min(map.length - 1, (int) Math.floor(wall.maxX)); x++) {
                for (int y = Math.max(0, (int) Math.floor(wall.minY)); y <= Math.min(map[x].length - 1, (int) Math.floor(wall.maxY)); y++) {
                    if (wallsByTile[x][y] == null)
                        wallsByTile[x][y] = new ArrayList<>();
                    wallsByTile[x][y].add(wall);
                }
            }
        }
    }

    public int[][] getMap() {
        return map;
    }

    /** Reuses the caller's hit object; no per-ray allocations. Distance is projection depth. */
    public boolean raycast(double originX, double originY, double dirX, double dirY, Hit hit) {
        hit.distance = Double.POSITIVE_INFINITY;
        hit.textureId = 0;
        hit.thinWall = false;
        if (dirX == 0 && dirY == 0)
            return false;
        int x = (int) Math.floor(originX), y = (int) Math.floor(originY);
        int stepX = dirX < 0 ? -1 : 1, stepY = dirY < 0 ? -1 : 1;
        double deltaX = dirX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1 / dirX);
        double deltaY = dirY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1 / dirY);
        double nextX = dirX == 0 ? Double.POSITIVE_INFINITY
                : (dirX < 0 ? originX - x : x + 1 - originX) * deltaX;
        double nextY = dirY == 0 ? Double.POSITIVE_INFINITY
                : (dirY < 0 ? originY - y : y + 1 - originY) * deltaY;
        double entry = 0;
        int side = 0;
        while (inBounds(x, y)) {
            if (map[x][y] > 0) {
                setHit(hit, entry, originX, originY, dirX, dirY, x, y, side, map[x][y], false);
                return true;
            }
            double exit = Math.min(nextX, nextY);
            List<WallSegment> walls = hasWalls ? wallsByTile[x][y] : null;
            if (walls != null) {
                for (int i = 0; i < walls.size(); i++)
                    intersect(walls.get(i), originX, originY, dirX, dirY, x, y, entry, exit, hit);
                if (hit.distance <= exit)
                    return true;
            }
            if (nextX < nextY) {
                entry = nextX;
                nextX += deltaX;
                x += stepX;
                side = 0;
            } else {
                entry = nextY;
                nextY += deltaY;
                y += stepY;
                side = 1;
            }
        }
        return false;
    }

    private static void intersect(WallSegment wall, double ox, double oy, double dx, double dy,
                                  int x, int y, double entry, double exit, Hit hit) {
        if ((dx == 0 && (ox < wall.minX || ox > wall.maxX))
                || (dy == 0 && (oy < wall.minY || oy > wall.maxY)))
            return;
        double nearX = Double.NEGATIVE_INFINITY, farX = Double.POSITIVE_INFINITY;
        double nearY = Double.NEGATIVE_INFINITY, farY = Double.POSITIVE_INFINITY;
        if (dx != 0) {
            double a = (wall.minX - ox) / dx, b = (wall.maxX - ox) / dx;
            nearX = Math.min(a, b);
            farX = Math.max(a, b);
        }
        if (dy != 0) {
            double a = (wall.minY - oy) / dy, b = (wall.maxY - oy) / dy;
            nearY = Math.min(a, b);
            farY = Math.max(a, b);
        }
        double near = Math.max(nearX, nearY), far = Math.min(farX, farY);
        if (far <= 0 || far < near)
            return;
        // When inside a partition, render its exit face rather than a negative hit.
        double distance = near >= 0 ? near : far;
        int side = near >= 0 ? (nearX >= nearY ? 0 : 1) : (farX <= farY ? 0 : 1);
        if (distance >= entry - 1e-9 && distance <= exit && distance < hit.distance)
            setHit(hit, distance, ox, oy, dx, dy, x, y, side, wall.textureId, true);
    }

    private static void setHit(Hit hit, double distance, double ox, double oy, double dx, double dy,
                               int x, int y, int side, int texture, boolean thin) {
        hit.distance = distance;
        hit.x = ox + distance * dx;
        hit.y = oy + distance * dy;
        hit.tileX = x;
        hit.tileY = y;
        hit.side = side;
        hit.textureId = texture;
        hit.thinWall = thin;
    }

    /** Axis-separated swept movement preserves sliding and cannot jump across a thin wall. */
    public boolean canMove(double fromX, double fromY, double toX, double toY) {
        if (toX < 0 || toY < 0 || !inBounds((int) toX, (int) toY) || map[(int) toX][(int) toY] != 0)
            return false;
        if (!hasWalls)
            return true;
        if (fromX != toX && fromY != toY)
            throw new IllegalArgumentException("Resolve movement one axis at a time");
        int firstX = Math.max(0, (int) Math.floor(Math.min(fromX, toX)));
        int lastX = Math.min(map.length - 1, (int) Math.floor(Math.max(fromX, toX)));
        for (int x = firstX; x <= lastX; x++) {
            int firstY = Math.max(0, (int) Math.floor(Math.min(fromY, toY)));
            int lastY = Math.min(map[x].length - 1, (int) Math.floor(Math.max(fromY, toY)));
            for (int y = firstY; y <= lastY; y++) {
                List<WallSegment> walls = wallsByTile[x][y];
                if (walls == null)
                    continue;
                for (int i = 0; i < walls.size(); i++) {
                    WallSegment wall = walls.get(i);
                    if (fromY == toY && fromY > wall.minY && fromY < wall.maxY
                            && Math.max(fromX, toX) > wall.minX && Math.min(fromX, toX) < wall.maxX)
                        return false;
                    if (fromX == toX && fromX > wall.minX && fromX < wall.maxX
                            && Math.max(fromY, toY) > wall.minY && Math.min(fromY, toY) < wall.maxY)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < map.length && y >= 0 && y < map[x].length;
    }
}
