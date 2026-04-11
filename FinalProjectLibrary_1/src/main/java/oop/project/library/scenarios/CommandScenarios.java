package oop.project.library.scenarios;

import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }
        if (basic.positional().size() != 2) {
            throw new RuntimeException(
                    "Expected exactly 2 positional arguments, got " + basic.positional().size() + "."
            );
        }

        int left = parseInteger(basic.positional().get(0), "left");
        int right = parseInteger(basic.positional().get(1), "right");

        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        if (!basic.positional().isEmpty()) {
            throw new RuntimeException("Unexpected positional arguments.");
        }

        Set<String> allowed = Set.of("left", "right");
        for (String key : basic.named().keySet()) {
            if (!allowed.contains(key)) {
                throw new RuntimeException("Unknown named argument '--" + key + "'.");
            }
        }

        if (!basic.named().containsKey("left")) {
            throw new RuntimeException("Missing required named argument '--left'.");
        }
        if (!basic.named().containsKey("right")) {
            throw new RuntimeException("Missing required named argument '--right'.");
        }

        double left = parseDouble(requireValue(basic.named().get("left"), "left"), "left");
        double right = parseDouble(requireValue(basic.named().get("right"), "right"), "right");

        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }
        if (basic.positional().size() > 1) {
            throw new RuntimeException(
                    "Expected at most 1 positional argument, got " + basic.positional().size() + "."
            );
        }

        String message = basic.positional().isEmpty()
                ? "echo,echo,echo..."
                : basic.positional().get(0);

        return Map.of("message", message);
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        if (basic.positional().size() != 1) {
            throw new RuntimeException(
                    "Expected exactly 1 positional argument, got " + basic.positional().size() + "."
            );
        }

        String term = basic.positional().get(0);
        Boolean caseInsensitive = false;

        for (String key : basic.named().keySet()) {
            if (!key.equals("case-insensitive") && !key.equals("i")) {
                throw new RuntimeException("Unknown named argument '--" + key + "'.");
            }
        }

        boolean hasLong = basic.named().containsKey("case-insensitive");
        boolean hasShort = basic.named().containsKey("i");

        if (hasLong && hasShort) {
            throw new RuntimeException("Duplicate case-insensitive flag.");
        }

        if (hasLong || hasShort) {
            String raw = hasLong
                    ? basic.named().get("case-insensitive")
                    : basic.named().get("i");

            if (raw == null || raw.isBlank()) {
                caseInsensitive = true;
            } else {
                caseInsensitive = parseBoolean(raw, "case-insensitive");
            }
        }

        return Map.of(
                "term", term,
                "case-insensitive", caseInsensitive
        );
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        if (!basic.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }
        if (basic.positional().size() != 2) {
            throw new RuntimeException(
                    "Expected exactly 2 positional arguments, got " + basic.positional().size() + "."
            );
        }

        String type = basic.positional().get(0);
        String rawValue = basic.positional().get(1);

        return switch (type) {
            case "static" -> Map.of(
                    "type", "static",
                    "value", parseInteger(rawValue, "value")
            );
            case "dynamic" -> Map.of(
                    "type", "dynamic",
                    "value", rawValue
            );
            default -> throw new RuntimeException("Unknown dispatch type '" + type + "'.");
        };
    }

    // Helpers

    private static BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }

    private static String requireValue(String value, String argName) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Missing value for argument '" + argName + "'.");
        }
        return value;
    }

    private static int parseInteger(String value, String argName) {
        if (value.contains(".")) {
            throw new RuntimeException(
                    "Argument '" + argName + "' must be an integer, got '" + value + "'."
            );
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Argument '" + argName + "' must be an integer, got '" + value + "'."
            );
        }
    }

    private static double parseDouble(String value, String argName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Argument '" + argName + "' must be a number, got '" + value + "'."
            );
        }
    }

    private static boolean parseBoolean(String value, String argName) {
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        throw new RuntimeException(
                "Argument '" + argName + "' must be true or false, got '" + value + "'."
        );
    }

}
