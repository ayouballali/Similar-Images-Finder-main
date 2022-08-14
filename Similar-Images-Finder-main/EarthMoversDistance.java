package src;

public class EarthMoversDistance {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -5406732779747414922L;

    /** {@inheritDoc} */
    public static double compute(double[] a, double[] b) {
        double lastDistance = 0;
        double totalDistance = 0;
        for (int i = 0; i < a.length; i++) {
            final double currentDistance = (a[i] + lastDistance) - b[i];
            totalDistance += Math.abs(currentDistance);
            lastDistance = currentDistance;
        }
        return totalDistance;
    }
}