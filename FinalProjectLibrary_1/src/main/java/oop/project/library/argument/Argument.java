package oop.project.library.argument;

/**
 * Polymorphic abstraction for parsing a raw String into a typed value T.
 * The only responsibility of an Argument is String -> Parsed Value.
 * It has no knowledge of argument names, positions, flags, or command structure.
 *
 * @param <T> the target parsed type
 */
public interface Argument<T> {

    /**
     * Parses the raw string input into a value of type T.
     * Throws {@link ArgumentParseException} on any parse or validation failure.
     *
     * @param input the raw string to parse
     * @return the parsed value
     * @throws ArgumentParseException if parsing or validation fails
     */
    T parse(String input);

}
