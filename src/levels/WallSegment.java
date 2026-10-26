package levels;

/** An axis-aligned, full-height partition, measured in map tile units. */
public final class WallSegment {
    public final double minX, minY, maxX, maxY;
    public final int textureId;

    public WallSegment(double x1, double y1, double x2, double y2, int textureId, double thickness) {
        if (!Double.isFinite(x1) || !Double.isFinite(y1) || !Double.isFinite(x2) || !Double.isFinite(y2)
                || !Double.isFinite(thickness) || thickness <= 0 || thickness > 1
                || textureId < 1 || ((x1 == x2) == (y1 == y2)))
            throw new IllegalArgumentException("Walls need distinct axis-aligned endpoints, a texture ID, and thickness in (0, 1]");
        this.textureId = textureId;
        double half = thickness / 2;
        minX = Math.min(x1, x2) - (x1 == x2 ? half : 0);
        maxX = Math.max(x1, x2) + (x1 == x2 ? half : 0);
        minY = Math.min(y1, y2) - (y1 == y2 ? half : 0);
        maxY = Math.max(y1, y2) + (y1 == y2 ? half : 0);
    }
}
