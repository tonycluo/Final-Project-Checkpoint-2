package oop.project.library.argument;

/**
 * Factory class providing predefined {@link Argument} instances for common types.
 * Use these as the starting point when defining command arguments.
 *
 * <p>Example usage:
 * <pre>
 *   Arguments.integer().inRange(1, 100)   // integer between 1 and 100
 *   Arguments.string().choices("easy", "normal", "hard")  // enum-like string
 *   Arguments.doub().inRange(0.0, 1.0)   // double between 0.0 and 1.0
 * </pre>
 */
public final class Arguments {

    private Arguments() {}

    /**
     * Returns an {@link IntegerArgument} that parses whole integers.
     * Rejects decimal inputs like "1.0". Chain {@link IntegerArgument#inRange}
     * to add range validation.
     */
    public static IntegerArgument integer() {
        return new IntegerArgument();
    }

    /**
     * Returns a {@link DoubleArgument} that parses decimal numbers.
     * Chain {@link DoubleArgument#inRange} to add range validation.
     */
    public static DoubleArgument doub() {
        return new DoubleArgument();
    }

    /**
     * Returns a {@link BooleanArgument} that accepts only "true" or "false".
     */
    public static BooleanArgument bool() {
        return new BooleanArgument();
    }

    /**
     * Returns a {@link StringArgument} that accepts any string.
     * Chain {@code .choices(...)} for enum-like validation or
     * {@code .regex(...)} for pattern-based validation.
     */
    public static StringArgument string() {
        return new StringArgument();
    }
}
