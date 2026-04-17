package oop.project.library.argument;

public final class Arguments {

    private Arguments() {}

    public static Argument<Integer> integer() {
        return new IntegerArgument();
    }

    public static Argument<Double> doub() {
        return new DoubleArgument();
    }

    public static Argument<Boolean> bool() {
        return new BooleanArgument();
    }

    public static Argument<String> string() {
        return new StringArgument();
    }
}
