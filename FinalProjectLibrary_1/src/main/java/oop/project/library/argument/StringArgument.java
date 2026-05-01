package oop.project.library.argument;

/**
 * Parses a raw string as a {@link String} — returns the input unchanged.
 * Optionally constrained to a set of allowed values via {@link #choices(String...)}
 * or a regex pattern via {@link #regex(String)}.
 */
public final class StringArgument implements Argument<String> {

    private final String[] choices;
    private final String regexPattern;

    StringArgument() {
        this.choices = null;
        this.regexPattern = null;
    }

    private StringArgument(String[] choices, String regexPattern) {
        this.choices = choices;
        this.regexPattern = regexPattern;
    }

    /**
     * Returns a new {@link StringArgument} that validates the parsed value
     * (case-insensitively) against the given allowed choices, returning the
     * value in lowercase on success.
     *
     * @param choices the allowed string values (case-insensitive)
     * @return a choices-constrained StringArgument
     */
    public StringArgument choices(String... choices) {
        return new StringArgument(choices, null);
    }

    /**
     * Returns a new {@link StringArgument} that validates the parsed value
     * matches the given regular expression pattern.
     *
     * @param pattern the regex pattern the value must fully match
     * @return a regex-constrained StringArgument
     */
    public StringArgument regex(String pattern) {
        return new StringArgument(null, pattern);
    }

    @Override
    public String parse(String input)  {
        if (choices != null) {
            String lower = input.toLowerCase();
            for (String choice : choices) {
                if (choice.equalsIgnoreCase(input)) {
                    return lower;
                }
            }
            throw new ArgumentParseException(
                    "Expected one of " + java.util.Arrays.toString(choices) +
                            " (case-insensitive), got '" + input + "'."
            );
        }

        if (regexPattern != null) {
            if (!input.matches(regexPattern)) {
                throw new ArgumentParseException(
                        "Expected value matching '" + regexPattern + "', got '" + input + "'."
                );
            }
        }

        return input;
    }
}
