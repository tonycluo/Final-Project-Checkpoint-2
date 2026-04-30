package oop.project.library.scenarios;

import oop.project.library.argument.Arguments;
import oop.project.library.command.Command;
import oop.project.library.command.CommandArgument;
import oop.project.library.command.CommandResult;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        Command command = new Command("mul")
                .addPositionalArgument(CommandArgument.positional("left", Arguments.integer()))
                .addPositionalArgument(CommandArgument.positional("right", Arguments.integer()));

        CommandResult result = parse(command, basic);

        int left = result.getInt("left");
        int right = result.getInt("right");

        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        Command command = new Command("div")
                .addNamedArgument(CommandArgument.named("left", Arguments.doub()))
                .addNamedArgument(CommandArgument.named("right", Arguments.doub()));

        CommandResult result = parse(command, basic);

        double left = result.getDouble("left");
        double right = result.getDouble("right");

        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        Command command = new Command("echo")
                .addPositionalArgument(
                        CommandArgument.positional("message", Arguments.string())
                                .optional("echo,echo,echo...")
                );

        CommandResult result = parse(command, basic);

        String message = result.getString("message");

        return Map.of("message", message);
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        Command command = new Command("search")
                .addPositionalArgument(CommandArgument.positional("term", Arguments.string()))
                .addNamedArgument(
                        CommandArgument.named("case-insensitive", Arguments.bool())
                                .alias("i")
                                .optional(false)
                                .implicit(true)
                );

        CommandResult result = parse(command, basic);

        String term = result.getString("term");
        boolean caseInsensitive = result.getBoolean("case-insensitive");

        return Map.of(
                "term", term,
                "case-insensitive", caseInsensitive
        );
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        BasicArgs basic = parseInput(arguments);

        Command staticCommand = new Command("static")
                .addPositionalArgument(CommandArgument.positional("value", Arguments.integer()));

        Command dynamicCommand = new Command("dynamic")
                .addPositionalArgument(CommandArgument.positional("value", Arguments.string()));

        Command command = new Command("dispatch")
                .addSubcommand("static", staticCommand)
                .addSubcommand("dynamic", dynamicCommand);

        CommandResult result = parse(command, basic);

        String type = result.getString("type");

        if (type.equals("static")) {
            int value = result.getInt("value");
            return Map.of("type", type, "value", value);
        }

        if (type.equals("dynamic")) {
            String value = result.getString("value");
            return Map.of("type", type, "value", value);
        }

        throw new RuntimeException("Unknown dispatch type: " + type);
    }

    public static Map<String, Object> showcase(String arguments) throws RuntimeException {
        Command command = new Command("search")
                .addPositionalArgument(CommandArgument.positional("term", Arguments.string()))
                .addNamedArgument(
                        CommandArgument.named("case-insensitive", Arguments.bool())
                                .alias("i")
                                .optional(false)
                                .implicit(true)
                );

        return Map.of("usage", command.usage());
    }

    private static BasicArgs parseInput(String arguments) {
        try {
            return new Input(arguments).parseBasicArgs();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        }
    }

    private static CommandResult parse(Command command, BasicArgs basic) {
        try {
            return command.parse(basic);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
