package oop.project.library.argument;

/**
 * Parses a raw string into an {@link Integer}.
 * Rejects values containing a decimal point (e.g. "1.0") so that
 * doubles are not silently accepted where integers are expected.
 *
 * <p>Supports optional inclusive range validation via {@link #inRange(int, int)}.
 */
public final class IntegerArgument implements Argument<Integer> {

    private final int min;
    private final int max;
    private final boolean ranged;

    IntegerArgument() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.ranged = false;
    }

    private IntegerArgument(int min, int max) {
        this.min = min;
        this.max = max;
        this.ranged = true;
    }

    /**
     * Returns a new {@link IntegerArgument} that additionally validates the parsed
     * value is within [{@code min}, {@code max}] inclusive.
     *
     * @param min the minimum allowable value (inclusive)
     * @param max the maximum allowable value (inclusive)
     * @return a range-constrained IntegerArgument
     */
    public IntegerArgument inRange(int min, int max) {
        return new IntegerArgument(min, max);
    }

    @Override
    public Integer parse(String input)  {
        if (input.contains(".")) {
            throw new ArgumentParseException("Expected integer, got '" + input + "'.");
        }
        try {
            int value = Integer.parseInt(input);
            if (ranged && (value < min || value > max)) {
                throw new ArgumentParseException(
                        "Expected integer in range [" + min + ", " + max + "], got " + value + "."
                );
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Expected integer, got '" + input + "'.");
        }
    }
}
