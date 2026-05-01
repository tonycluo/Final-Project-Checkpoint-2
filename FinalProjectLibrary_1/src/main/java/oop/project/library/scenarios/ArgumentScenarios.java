package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentParseException;
import oop.project.library.argument.Arguments;
import oop.project.library.input.Input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

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

        int left  = parse(Arguments.integer(), basic.positional().get(0));
        int right = parse(Arguments.integer(), basic.positional().get(1));

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

        double left  = parse(Arguments.doub(), basic.positional().get(0));
        double right = parse(Arguments.doub(), basic.positional().get(1));

        return Map.of("left", left, "right", right);
    }

    // Parse one int in [1, 100]
    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        int number = parse(Arguments.integer().inRange(1, 100), basic.positional().getFirst());

        return Map.of("number", number);
    }

    // Parse one string matching the Difficulty enum: peaceful, easy, normal, hard (case-insensitive)
    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        String difficulty = parse(
                Arguments.string().choices("peaceful", "easy", "normal", "hard"),
                basic.positional().getFirst()
        );

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

        LocalDate date = parseDate(basic.positional().getFirst());

        return Map.of("date", date);
    }

    // Helpers

    private static oop.project.library.input.BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }

    // Call Argument.parse and convert ArgumentParseException to RuntimeException at the scenario boundary
    private static <T> T parse(oop.project.library.argument.Argument<T> argument, String raw) {
        try {
            return argument.parse(raw);
        } catch (oop.project.library.argument.ArgumentParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // LocalDate is a custom type not covered by the argument package; parsed directly here
    private static LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Not a valid date (expected yyyy-MM-dd), got '" + raw + "'.");
        }
    }
}
