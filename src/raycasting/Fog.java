package raycasting;

/** Exponential distance fog; density is per map tile, and zero disables fading. */
public final class Fog {
    // Quantize optical distance, not world distance, so the error bound holds
    // at every density. Rounding changes an RGB channel by at most one level.
    private static final int SAMPLES_PER_UNIT = 1024;
    private static final int MAX_OPTICAL_DISTANCE = 12;
    private static final int VISIBILITY_SCALE = 1 << 16;
    private static final int[] VISIBILITY = createVisibilityTable();

    private final double density;
    private final int color;

    public Fog(double density, int color) {
        if (!Double.isFinite(density) || density < 0)
            throw new IllegalArgumentException("Fog density must be finite and non-negative");
        this.density = density;
        this.color = color & 0xFFFFFF;
    }

    public int getColor() {
        return color;
    }

    public double amountAt(double distance) {
        if (density == 0 || distance <= 0)
            return 0;
        // No clear radius or far cutoff: light fades continuously along its path.
        return 1 - Math.exp(-density * distance);
    }

    /** Fast rendering path; square roots are intrinsic, exponentials are cached. */
    public int applyAtSquaredDistance(int surfaceColor, double distanceSquared) {
        if (density == 0 || distanceSquared <= 0)
            return surfaceColor & 0xFFFFFF;
        double opticalDistance = density * Math.sqrt(distanceSquared);
        if (opticalDistance >= MAX_OPTICAL_DISTANCE)
            return color;
        int index = (int) (opticalDistance * SAMPLES_PER_UNIT + .5);
        int visibility = VISIBILITY[index];
        int r = blendFixed((surfaceColor >> 16) & 0xFF, (color >> 16) & 0xFF, visibility);
        int g = blendFixed((surfaceColor >> 8) & 0xFF, (color >> 8) & 0xFF, visibility);
        int b = blendFixed(surfaceColor & 0xFF, color & 0xFF, visibility);
        return r << 16 | g << 8 | b;
    }

    private static int[] createVisibilityTable() {
        int[] table = new int[MAX_OPTICAL_DISTANCE * SAMPLES_PER_UNIT + 1];
        for (int i = 0; i < table.length; i++)
            table[i] = (int) Math.round(Math.exp(-(double) i / SAMPLES_PER_UNIT) * VISIBILITY_SCALE);
        return table;
    }

    private static int blendFixed(int surface, int fog, int visibility) {
        return fog + (((surface - fog) * visibility + VISIBILITY_SCALE / 2) >> 16);
    }

    public int blend(int surfaceColor, double amount) {
        int r = blendChannel((surfaceColor >> 16) & 0xFF, (color >> 16) & 0xFF, amount);
        int g = blendChannel((surfaceColor >> 8) & 0xFF, (color >> 8) & 0xFF, amount);
        int b = blendChannel(surfaceColor & 0xFF, color & 0xFF, amount);
        return r << 16 | g << 8 | b;
    }

    private int blendChannel(int surface, int fog, double amount) {
        return (int) Math.round(surface + (fog - surface) * amount);
    }
}
