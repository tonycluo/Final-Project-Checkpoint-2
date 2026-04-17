package oop.project.library.argument;

public final class StringArgument implements Argument<String> {

    @Override
    public String parse(String input) {
        return input;
    }
}
