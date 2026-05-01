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

        int left  = parse(INTEGER, basic.positional().get(0));
        int right = parse(INTEGER, basic.positional().get(1));

        return Map.of("left", left, "right", right);
    }

    // Parse left and right double arguments
    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 2) {
            throw new RuntimeException("Expected exactly 2 positional arguments, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        double left  = parse(DOUBLE, basic.positional().get(0));
        double right = parse(DOUBLE, basic.positional().get(1));

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

        int number = parse(INTEGER.inRange(1, 100), basic.positional().getFirst());

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

        String difficulty = parse(enumChoice("peaceful", "easy", "normal", "hard"), basic.positional().getFirst());

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

        LocalDate date = parse(LOCAL_DATE, basic.positional().getFirst());

        return Map.of("date", date);
    }

    // Custom checked exception for failure to parse or validate an argument value. Scenarios catch this and re-throw as RuntimeException
    static final class ArgumentException extends Exception {
        ArgumentException(String message) {
            super(message);
        }
    }

    /*
     Polymorphic abstraction:
     Parse a raw string into typed value T with optional appended validators.
     Type conversion and validation logic is reusable and separate from scenarios.
    */
    static final class Argument<T> {

        private final Function<String, T> parser;
        private final List<Validator<T>> validators;

        private Argument(Function<String, T> parser, List<Validator<T>> validators) {
            this.parser = parser;
            this.validators = validators;
        }

        /*
         Create a new Argument with the given parsing function.
         The parser throws any exception on failure, which will be
         caught and wrapped in an ArgumentException.
        */
        static <T> Argument<T> of(Function<String, T> parser) {
            return new Argument<>(parser, new ArrayList<>());
        }


        //Return a new Argument with the given validation rule appended, check validators after valid parsing
        Argument<T> validate(Predicate<T> predicate, String message) {
            var next = new ArrayList<>(validators);
            next.add(new Validator<>(predicate, message));
            return new Argument<>(parser, next);
        }

        /*
         Parse the raw string into T and runs all validators.
         Throw on parse failure or any failed validation, including a matching message.
        */
        T parse(String raw) throws ArgumentException {
            T value;
            try {
                value = parser.apply(raw);
            } catch (Exception e) {
                throw new ArgumentException("Failed to parse '" + raw + "': " + e.getMessage());
            }
            for (var v : validators) {
                if (!v.predicate().test(value)) {
                    throw new ArgumentException("Invalid value '" + raw + "': " + v.message());
                }
            }
            return value;
        }

        private record Validator<T>(Predicate<T> predicate, String message) {}
    }

    /*
     Abstract range checking for Integer and Double
     Helps with range validation using reused logic
    */
    static final class NumericArgument<N extends Comparable<N>> {

        private final Argument<N> inner;

        private NumericArgument(Argument<N> inner) {
            this.inner = inner;
        }

        static <N extends Comparable<N>> NumericArgument<N> of(Function<String, N> parser) {
            return new NumericArgument<>(Argument.of(parser));
        }

        //Return a new NumericArgument within [min, max] inclusive for any comparable numeric type (int, double, etc)
        NumericArgument<N> inRange(N min, N max) {
            return new NumericArgument<>(inner.validate(
                    n -> n.compareTo(min) >= 0 && n.compareTo(max) <= 0,
                    "must be between " + min + " and " + max + " (inclusive)"
            ));
        }

        NumericArgument<N> validate(Predicate<N> predicate, String message) {
            return new NumericArgument<>(inner.validate(predicate, message));
        }

        N parse(String raw) throws ArgumentException {
            return inner.parse(raw);
        }
    }

    // Parse an int; reject decimals
    private static final NumericArgument<Integer> INTEGER = NumericArgument.of(raw -> {
        if (raw.contains(".")) {
            throw new IllegalArgumentException("not an integer (contains decimal point)");
        }
        return Integer.parseInt(raw);
    });

    // Parse a double.
    private static final NumericArgument<Double> DOUBLE = NumericArgument.of(Double::parseDouble);

    // Parse a LocalDate in yyyy-MM-dd format
    private static final Argument<LocalDate> LOCAL_DATE = Argument.of(raw -> {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("not a valid date (expected yyyy-MM-dd)");
        }
    });

    // Return case-insensitive argument from given choices
    private static Argument<String> enumChoice(String... choices) {
        var allowed = Set.of(choices);
        return Argument.of(raw -> {
            String lower = raw.toLowerCase();
            if (!allowed.contains(lower)) {
                throw new IllegalArgumentException("must be one of " + allowed + ", instead received '" + raw + "'");
            }
            return lower;
        });
    }

    //Return argument that validates regex pattern
    private static Argument<String> regex(String pattern) {
        var compiled = Pattern.compile(pattern);
        return Argument.of(raw -> {
            if (!compiled.matcher(raw).matches()) {
                throw new IllegalArgumentException("must match pattern '" + pattern + "', instead received '" + raw + "'");
            }
            return raw;
        });
    }


    // Call parse(String) and convert ArgumentException to RuntimeException
    private static <T> T parse(Argument<T> argument, String raw) throws RuntimeException {
        try {
            return argument.parse(raw);
        } catch (ArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Overload of parse(Argument, String) for NumericArgument
    private static <N extends Comparable<N>> N parse(NumericArgument<N> argument, String raw) throws RuntimeException {
        try {
            return argument.parse(raw);
        } catch (ArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static oop.project.library.input.BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }
}
