package oop.project.library.argument;

/**
 * Parses a raw string into a {@link Double}.
 * Accepts all values parseable by {@link Double#parseDouble}, including
 * scientific notation, {@code Infinity}, and {@code NaN}.
 *
 * <p>Supports optional inclusive range validation via {@link #inRange(double, double)}.
 */
public final class DoubleArgument implements Argument<Double> {

    private final double min;
    private final double max;
    private final boolean ranged;

    DoubleArgument() {
        this.min = Double.NEGATIVE_INFINITY;
        this.max = Double.POSITIVE_INFINITY;
        this.ranged = false;
    }

    private DoubleArgument(double min, double max) {
        this.min = min;
        this.max = max;
        this.ranged = true;
    }

    /**
     * Returns a new {@link DoubleArgument} that additionally validates the parsed
     * value is within [{@code min}, {@code max}] inclusive.
     *
     * @param min the minimum allowable value (inclusive)
     * @param max the maximum allowable value (inclusive)
     * @return a range-constrained DoubleArgument
     */
    public DoubleArgument inRange(double min, double max) {
        return new DoubleArgument(min, max);
    }

    @Override
    public Double parse(String input)  {
        try {
            double value = Double.parseDouble(input);
            if (ranged && (value < min || value > max)) {
                throw new ArgumentParseException(
                        "Expected number in range [" + min + ", " + max + "], got " + value + "."
                );
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Expected number, got '" + input + "'.");
        }
    }
}
