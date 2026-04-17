package oop.project.library.scenarios;

import oop.project.library.input.Input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ArgumentScenarios {

    // Parse left and right int arguments. Reject floats, non-number strings, and non-2 arguments
    public static Map<String, Object> add(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 2) {
            throw new RuntimeException("Expected exactly 2 positional arguments, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        int left  = INTEGER.parse(basic.positional().get(0));
        int right = INTEGER.parse(basic.positional().get(1));

        return Map.of("left", left, "right", right);
    }

    // Parse left and right double arguments, reject negative decimal inputs to mirror known bug
    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 2) {
            throw new RuntimeException("Expected exactly 2 positional arguments, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        double left  = SUB_DOUBLE.parse(basic.positional().get(0));
        double right = SUB_DOUBLE.parse(basic.positional().get(1));

        return Map.of("left", left, "right", right);
    }

    // Parse one int in 1-100 range
    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        int number = INTEGER.validate(n -> n >= 1 && n <= 100, "must be between 1 and 100 (inclusive)")
                .parse(basic.positional().getFirst());

        return Map.of("number", number);
    }

    // Parse one string matching the string-choice constraint(difficulty)
    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        String difficulty = enumChoice("peaceful", "easy", "normal", "hard")
                .parse(basic.positional().getFirst());

        return Map.of("difficulty", difficulty);
    }

    // Parse one LocalDate in yyyy-MM-dd format
    public static Map<String, Object> date(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        LocalDate date = LOCAL_DATE.parse(basic.positional().getFirst());

        return Map.of("date", date);
    }

    /*
     Polymorphic abstraction:
     Parse a raw string into typed value T with optional appended validators.
     Type conversion and validation logic is reusable and separate from scenarios.
     */
    private static final class Argument<T> {

        private final Function<String, T> parser;
        private final List<Validator<T>> validators;

        private Argument(Function<String, T> parser, List<Validator<T>> validators) {
            this.parser = parser;
            this.validators = validators;
        }

        static <T> Argument<T> of(Function<String, T> parser) {
            return new Argument<>(parser, new ArrayList<>());
        }

        // Return a new Argument<T> with appended validators
        Argument<T> validate(Predicate<T> predicate, String message) {
            var next = new ArrayList<>(validators);
            next.add(new Validator<>(predicate, message));
            return new Argument<>(parser, next);
        }

        // Parse raw input, check against all validators. Throw RuntimeException on failure
        T parse(String raw) {
            T value;
            try {
                value = parser.apply(raw);
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to parse '" + raw + "': " + e.getMessage());
            }
            for (var v : validators) {
                if (!v.predicate().test(value)) {
                    throw new RuntimeException("Invalid value '" + raw + "': " + v.message());
                }
            }
            return value;
        }

        private record Validator<T>(Predicate<T> predicate, String message) {}
    }

    // Helper function: parse ints, reject decimals
    private static final Argument<Integer> INTEGER = Argument.of(raw -> {
        if (raw.contains(".")) {
            throw new RuntimeException("not an integer (contains decimal point)");
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new RuntimeException("not a valid integer");
        }
    });

    // Helper function for handing doubles with the negative decimal bug
    private static final Argument<Double> SUB_DOUBLE = Argument.of(raw -> {
        if (raw.startsWith("-") && raw.contains(".")) {
            throw new RuntimeException("negative decimals are not supported (known bug)");
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new RuntimeException("not a valid number");
        }
    });

    // Helper function that sets LOCAL_DATE
    private static final Argument<LocalDate> LOCAL_DATE = Argument.of(raw -> {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("not a valid date (expected yyyy-MM-dd)");
        }
    });

    // Helper function that creates enum constraint & checks against it
    private static Argument<String> enumChoice(String... choices) {
        var allowed = Set.of(choices);
        return Argument.of(raw -> {
            String lower = raw.toLowerCase();
            if (!allowed.contains(lower)) {
                throw new RuntimeException("must be one of " + allowed + ", instead received '" + raw + "'");
            }
            return lower;
        });
    }

    // Regex function
    private static Argument<String> regex(String pattern) {
        var compiled = Pattern.compile(pattern);
        return Argument.of(raw -> {
            if (!compiled.matcher(raw).matches()) {
                throw new RuntimeException("must match pattern '" + pattern + "', instead received '" + raw + "'");
            }
            return raw;
        });
    }

    // Helper function for string parsing
    private static oop.project.library.input.BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }
}
