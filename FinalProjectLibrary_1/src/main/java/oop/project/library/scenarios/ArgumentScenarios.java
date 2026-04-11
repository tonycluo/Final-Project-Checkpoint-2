package oop.project.library.scenarios;

import oop.project.library.input.Input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

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

        int left  = parseInteger(basic.positional().get(0), "left");
        int right = parseInteger(basic.positional().get(1), "right");

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

        double left  = parseSubDouble(basic.positional().get(0), "left");
        double right = parseSubDouble(basic.positional().get(1), "right");

        return Map.of("left", left, "right", right);
    }

    // Parse one int > 0
    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        int number = parseInteger(basic.positional().get(0), "number");

        if (number <= 0) {
            throw new RuntimeException("Argument 'number' must be a positive integer, instead received" + number + ".");
        }

        return Map.of("number", number);
    }

    // Parse "easy", "normal", or "hard" string
    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        var basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException("Expected exactly 1 positional argument, instead received " + basic.positional().size() + ".");
        }
        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }

        String difficulty = basic.positional().get(0);
        var allowed = Set.of("easy", "normal", "hard");

        if (!allowed.contains(difficulty)) {
            throw new RuntimeException("Argument 'difficulty' must be one of " + allowed + ", instead received '" + difficulty + "'.");
        }

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

        String raw = basic.positional().get(0);

        try {
            LocalDate date = LocalDate.parse(raw);
            return Map.of("date", date);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Argument 'date' is not valid (expected yyyy-MM-dd), instead received '" + raw + "'.");
        }
    }

    // Helper functions

    private static oop.project.library.input.BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }

    // Parse string, convert to int. Reject decimal point inputs
    private static int parseInteger(String value, String argName) {
        if (value.contains(".")) {
            throw new RuntimeException("Argument '" + argName + "' must be an integer, instead received '" + value + "'.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Argument '" + argName + "' must be an integer, instead received '" + value + "'.");
        }
    }

    // Parse double inputs for subtraction function

    private static double parseSubDouble(String value, String argName) {
        if (value.startsWith("-") && value.contains(".")) {
            throw new RuntimeException("Argument '" + argName + "' cannot be a negative decimal (known bug), instead received '" + value + "'.");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Argument '" + argName + "' must be a number, instead received '" + value + "'.");
        }
    }
}
