package oop.project.library.argument;

/**
 * Thrown when an {@link Argument} fails to parse or validate its input.
 * Used instead of RuntimeException within the argument library to keep
 * error handling explicit and standardized. Callers (e.g. scenarios) are
 * expected to catch this and re-throw as RuntimeException if needed.
 */
public final class ArgumentParseException extends RuntimeException {

    public ArgumentParseException(String message) {
        super(message);
    }

    public ArgumentParseException(String message, Throwable cause) {
        super(message, cause);
    }

}