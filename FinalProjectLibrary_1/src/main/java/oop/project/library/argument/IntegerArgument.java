package oop.project.library.argument;

public final class IntegerArgument implements Argument<Integer> {

    @Override
    public Integer parse(String input) {
        if (input.contains(".")) {
            throw new RuntimeException("Expected integer, got '" + input + "'.");
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Expected integer, got '" + input + "'.");
        }
    }
}
