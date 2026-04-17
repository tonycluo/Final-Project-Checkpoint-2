package oop.project.library.argument;

public final class DoubleArgument implements Argument<Double> {

    @Override
    public Double parse(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Expected number, got '" + input + "'.");
        }
    }
}
