package ranger.distribution;

/**
 * Normal Distribution.
 */
public class NormalDistribution implements Distribution {

    private final org.apache.commons.math3.distribution.NormalDistribution delegate;
    private final double lower;
    private final double upper;
    private final double innerRange;

    /**
     * Constructs Normal distribution.
     * <code>mean</code> is set to 0.5, <code>standardDeviation</code> is set to 0.125, <code>lower</code> is
     * set to 0 and <code>upper</code> is set to 1.
     */
    public NormalDistribution() {
        this(0.5, 0.125, 0, 1);
    }

    /**
     * Constructs Normal distribution with specified <code>mean</code>, <code>standardDeviation</code>,
     * <code>lower</code> and <code>upper</code>.
     * @param mean Mean of Normal distribution.
     * @param standardDeviation Standard deviation of Normal distribution.
     * @param lower Lower bound, no values lower than this will be generated.
     * @param upper Upper bound, no values higher than this will be generated.
     * @throws ArithmeticException if mean is not within lower and upper
     */
    public NormalDistribution(double mean, double standardDeviation, double lower, double upper) {
        if(mean < lower || mean > upper) { throw new ArithmeticException("mean must be in between lower and upper bounds"); }
        this.delegate = new org.apache.commons.math3.distribution.NormalDistribution(mean, standardDeviation);
        this.lower = lower;
        this.upper = upper;
        this.innerRange = upper - lower;
    }

    @Override
    public int nextInt(int bound) {
        return (int) normalize(delegate.sample(), 0, bound);
    }

    @Override
    public int nextInt(int lower, int upper) {
        return (int) normalize(delegate.sample(), lower, upper);
    }

    @Override
    public long nextLong(long bound) {
        return (long) normalize(delegate.sample(), 0, bound);
    }

    @Override
    public long nextLong(long lower, long upper) {
        return (int) normalize(delegate.sample(), lower, upper);
    }

    @Override
    public double nextDouble(double lower, double upper) {
        return normalize(delegate.sample(), lower, upper);
    }

    @Override
    public boolean nextBoolean() {
        return ((long) normalize(delegate.sample(), 0, 100)) % 2 == 0;
    }

    @Override
    public NormalDistribution clone() {
        return new NormalDistribution(delegate.getMean(), delegate.getStandardDeviation(), this.lower, this.upper);
    }

    private double normalize(double value, double normalizationLowerBound, double normalizationUpperBound) {
        double boundedValue = boundValue(value);
        // normalize boundedValue to new range
        double normalizedRange = normalizationUpperBound - normalizationLowerBound;
        return (((boundedValue - lower) * normalizedRange) / innerRange) + normalizationLowerBound;
    }

    private double boundValue(double value) {
        double boundedValue = value;
        while (boundedValue < lower || boundedValue > upper) {  //skip all values that are outside range
            boundedValue = delegate.sample();
        }
        return boundedValue;
    }
}
