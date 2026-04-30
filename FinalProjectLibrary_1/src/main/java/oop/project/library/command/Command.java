package oop.project.library.command;

import oop.project.library.input.BasicArgs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Command {

    private final String name;
    private final List<CommandArgument<?>> positionalArguments;
    private final List<CommandArgument<?>> namedArguments;
    private final Map<String, Command> subcommands;

    public Command(String name) {
        this.name = Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Command name cannot be blank.");
        }

        this.positionalArguments = new ArrayList<>();
        this.namedArguments = new ArrayList<>();
        this.subcommands = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Adds a positional argument to this command.
     *
     * Positional arguments are parsed according to their order in the input.
     *
     * @param argument the positional command argument to add
     * @return this command for chaining
     * @throws IllegalArgumentException if the argument is not positional or conflicts with an existing argument name/alias
     */
    public Command addPositionalArgument(CommandArgument<?> argument) {
        Objects.requireNonNull(argument, "argument");
        if (!argument.isPositional()) {
            throw new IllegalArgumentException("Expected a positional argument.");
        }

        ensureUniqueArgumentNames(argument);
        positionalArguments.add(argument);
        return this;
    }


    /**
     * Adds a named argument to this command.
     *
     * Named arguments are parsed using flag-style names such as --left or -i.
     *
     * @param argument the named command argument to add
     * @return this command for chaining
     * @throws IllegalArgumentException if the argument is not named or conflicts with an existing argument name/alias
     */
    public Command addNamedArgument(CommandArgument<?> argument) {
        Objects.requireNonNull(argument, "argument");
        if (!argument.isNamed()) {
            throw new IllegalArgumentException("Expected a named argument.");
        }

        ensureUniqueArgumentNames(argument);
        namedArguments.add(argument);
        return this;
    }

    public Command addSubcommand(String token, Command subcommand) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(subcommand, "subcommand");

        String normalized = normalizeName(token);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Subcommand token cannot be blank.");
        }
        if (subcommands.containsKey(normalized)) {
            throw new IllegalArgumentException("Duplicate subcommand: " + normalized);
        }

        subcommands.put(normalized, subcommand);
        return this;
    }

    public CommandResult parse(BasicArgs input) {
        Objects.requireNonNull(input, "input");

        if (!subcommands.isEmpty()) {
            return parseSubcommand(input);
        }

        List<String> rawPositionals = input.positional();
        Map<String, String> rawNamed = input.named();
        Map<String, Object> parsedValues = new LinkedHashMap<>();

        if (rawPositionals.size() > positionalArguments.size()) {
            throw new CommandParseException(
                    "Command '" + name + "' expected at most " + positionalArguments.size() +
                            " positional argument(s), but got " + rawPositionals.size() + "."
            );
        }

        for (int i = 0; i < positionalArguments.size(); i++) {
            CommandArgument<?> argument = positionalArguments.get(i);

            if (i < rawPositionals.size()) {
                parsedValues.put(argument.getName(), parseSingleArgument(argument, rawPositionals.get(i)));
            } else if (argument.hasDefaultValue()) {
                parsedValues.put(argument.getName(), argument.getDefaultValue());
            } else if (argument.isRequired()) {
                throw new CommandParseException(
                        "Missing required positional argument '" + argument.getName() +
                                "' for command '" + name + "'."
                );
            }
        }

        Map<String, ProvidedNamedValue> provided = collectNamedValues(rawNamed);

        for (CommandArgument<?> argument : namedArguments) {
            ProvidedNamedValue namedValue = provided.get(argument.getName());

            if (namedValue != null) {
                if (namedValue.value() == null) {
                    if (argument.hasImplicitValue()) {
                        parsedValues.put(argument.getName(), argument.getImplicitValue());
                    } else {
                        throw new CommandParseException(
                                "Missing value for named argument '--" + argument.getName() +
                                        "' for command '" + name + "'."
                        );
                    }
                } else {
                    parsedValues.put(argument.getName(), parseSingleArgument(argument, namedValue.value()));
                }
            } else if (argument.hasDefaultValue()) {
                parsedValues.put(argument.getName(), argument.getDefaultValue());
            } else if (argument.isRequired()) {
                throw new CommandParseException(
                        "Missing required named argument '--" + argument.getName() +
                                "' for command '" + name + "'."
                );
            }
        }

        return new CommandResult(name, parsedValues);
    }

    private CommandResult parseSubcommand(BasicArgs input) {
        List<String> rawPositionals = input.positional();

        if (rawPositionals.isEmpty()) {
            throw new CommandParseException(
                    "Missing required subcommand for command '" + name + "'."
            );
        }

        String token = normalizeName(rawPositionals.get(0));
        Command subcommand = subcommands.get(token);

        if (subcommand == null) {
            throw new CommandParseException(
                    "Unknown subcommand '" + token + "' for command '" + name + "'."
            );
        }

        List<String> remainingPositionals = rawPositionals.subList(1, rawPositionals.size());
        CommandResult subcommandResult = subcommand.parse(
                new BasicArgs(remainingPositionals, input.named())
        );

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("type", token);
        values.putAll(subcommandResult.asMap());
        return new CommandResult(name, values);
    }

    private Map<String, ProvidedNamedValue> collectNamedValues(Map<String, String> rawNamed) {
        Map<String, ProvidedNamedValue> provided = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : rawNamed.entrySet()) {
            String providedName = normalizeName(entry.getKey());
            CommandArgument<?> argument = findNamedArgument(providedName);

            if (argument == null) {
                throw new CommandParseException(
                        "Unknown named argument '--" + providedName + "' for command '" + name + "'."
                );
            }

            if (provided.containsKey(argument.getName())) {
                throw new CommandParseException(
                        "Duplicate named argument '--" + argument.getName() + "' for command '" + name + "'."
                );
            }

            provided.put(argument.getName(), new ProvidedNamedValue(entry.getValue()));
        }

        return provided;
    }

    private CommandArgument<?> findNamedArgument(String providedName) {
        for (CommandArgument<?> argument : namedArguments) {
            if (argument.matchesName(providedName)) {
                return argument;
            }
        }
        return null;
    }

    private void ensureUniqueArgumentNames(CommandArgument<?> candidate) {
        for (CommandArgument<?> existing : positionalArguments) {
            ensureNoOverlap(existing, candidate);
        }
        for (CommandArgument<?> existing : namedArguments) {
            ensureNoOverlap(existing, candidate);
        }
    }

    private void ensureNoOverlap(CommandArgument<?> existing, CommandArgument<?> candidate) {
        if (existing.getName().equals(candidate.getName())) {
            throw new IllegalArgumentException("Duplicate argument name: " + candidate.getName());
        }

        for (String alias : candidate.getAliases()) {
            if (existing.matchesName(alias)) {
                throw new IllegalArgumentException("Duplicate argument alias: " + alias);
            }
        }

        for (String alias : existing.getAliases()) {
            if (candidate.matchesName(alias)) {
                throw new IllegalArgumentException("Duplicate argument alias: " + alias);
            }
        }
    }

    private Object parseSingleArgument(CommandArgument<?> argument, String rawValue) {
        try {
            return argument.parse(rawValue);
        } catch (RuntimeException ex) {
            throw new CommandParseException(
                    "Failed to parse argument '" + argument.getName() +
                            "' from value '" + rawValue + "'.",
                    ex
            );
        }
    }

    private static String normalizeName(String value) {
        String normalized = value.trim();
        while (normalized.startsWith("-")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    /**
     * Returns a human-readable usage string for this command.
     *
     * This is a feature showcase beyond the assignment requirements. It allows the
     * command structure to describe itself without manually writing usage text.
     *
     * @return a usage string describing positional, named, optional, and alias arguments
     */
    public String usage() {
        StringBuilder builder = new StringBuilder(name);

        for (CommandArgument<?> argument : positionalArguments) {
            if (argument.hasDefaultValue()) {
                builder.append(" [<").append(argument.getName()).append(">]");
            } else {
                builder.append(" <").append(argument.getName()).append(">");
            }
        }

        for (CommandArgument<?> argument : namedArguments) {
            builder.append(" ");

            if (argument.hasDefaultValue()) {
                builder.append("[");
            }

            builder.append("--").append(argument.getName());

            for (String alias : argument.getAliases()) {
                builder.append("|-").append(alias);
            }

            if (!argument.hasImplicitValue()) {
                builder.append(" <value>");
            }

            if (argument.hasDefaultValue()) {
                builder.append("]");
            }
        }

        return builder.toString();
    }

    private record ProvidedNamedValue(String value) { }
}
