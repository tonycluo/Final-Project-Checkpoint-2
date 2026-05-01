package oop.project.library.argument;

/**
 * Parses a raw string into a {@link Boolean}.
 * Only the exact strings {@code "true"} and {@code "false"} are accepted.
 */
public final class BooleanArgument implements Argument<Boolean> {

    @Override
    public Boolean parse(String input)  {
        if ("true".equals(input)) {
            return true;
        }
        if ("false".equals(input)) {
            return false;
        }
        throw new ArgumentParseException("Expected true or false, got '" + input + "'.");
    }
}
