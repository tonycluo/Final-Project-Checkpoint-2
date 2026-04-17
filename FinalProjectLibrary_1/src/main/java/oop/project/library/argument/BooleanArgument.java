package oop.project.library.argument;

public final class BooleanArgument implements Argument<Boolean> {

    @Override
    public Boolean parse(String input) {
        if ("true".equals(input)) {
            return true;
        }
        if ("false".equals(input)) {
            return false;
        }
        throw new RuntimeException("Expected true or false, got '" + input + "'.");
    }
}
