package oop.project.library.command;

import oop.project.library.argument.Argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CommandArgument<T> {

    public enum Kind {
        POSITIONAL,
        NAMED
    }

    private final String name;
    private final Kind kind;
    private final Argument<T> parser;
    private final List<String> aliases;

    private final boolean required;
    private final boolean hasDefaultValue;
    private final T defaultValue;

    private final boolean hasImplicitValue;
    private final T implicitValue;

    private CommandArgument(
            String name,
            Kind kind,
            Argument<T> parser,
            List<String> aliases,
            boolean required,
            boolean hasDefaultValue,
            T defaultValue,
            boolean hasImplicitValue,
            T implicitValue
    ) {
        this.name = normalizeName(Objects.requireNonNull(name, "name"));
        this.kind = Objects.requireNonNull(kind, "kind");
        this.parser = Objects.requireNonNull(parser, "parser");

        List<String> normalizedAliases = new ArrayList<>();
        for (String alias : aliases) {
            normalizedAliases.add(normalizeName(Objects.requireNonNull(alias, "alias")));
        }

        this.aliases = Collections.unmodifiableList(normalizedAliases);
        this.required = required;
        this.hasDefaultValue = hasDefaultValue;
        this.defaultValue = defaultValue;
        this.hasImplicitValue = hasImplicitValue;
        this.implicitValue = implicitValue;
    }

    public static <T> CommandArgument<T> positional(String name, Argument<T> parser) {
        return new CommandArgument<>(
                name,
                Kind.POSITIONAL,
                parser,
                List.of(),
                true,
                false,
                null,
                false,
                null
        );
    }

    public static <T> CommandArgument<T> named(String name, Argument<T> parser) {
        return new CommandArgument<>(
                name,
                Kind.NAMED,
                parser,
                List.of(),
                true,
                false,
                null,
                false,
                null
        );
    }

    public CommandArgument<T> alias(String alias) {
        List<String> updatedAliases = new ArrayList<>(aliases);
        updatedAliases.add(alias);
        return new CommandArgument<>(
                name,
                kind,
                parser,
                updatedAliases,
                required,
                hasDefaultValue,
                defaultValue,
                hasImplicitValue,
                implicitValue
        );
    }

    public CommandArgument<T> optional(T defaultValue) {
        return new CommandArgument<>(
                name,
                kind,
                parser,
                aliases,
                false,
                true,
                defaultValue,
                hasImplicitValue,
                implicitValue
        );
    }

    public CommandArgument<T> implicit(T implicitValue) {
        return new CommandArgument<>(
                name,
                kind,
                parser,
                aliases,
                required,
                hasDefaultValue,
                defaultValue,
                true,
                implicitValue
        );
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public Argument<T> getParser() {
        return parser;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean hasDefaultValue() {
        return hasDefaultValue;
    }

    public T getDefaultValue() {
        if (!hasDefaultValue) {
            throw new IllegalStateException("Argument has no default value.");
        }
        return defaultValue;
    }

    public boolean hasImplicitValue() {
        return hasImplicitValue;
    }

    public T getImplicitValue() {
        if (!hasImplicitValue) {
            throw new IllegalStateException("Argument has no implicit value.");
        }
        return implicitValue;
    }

    public boolean isPositional() {
        return kind == Kind.POSITIONAL;
    }

    public boolean isNamed() {
        return kind == Kind.NAMED;
    }

    public T parse(String raw) {
        return parser.parse(raw);
    }

    public boolean matchesName(String candidate) {
        String normalized = normalizeName(candidate);
        if (name.equals(normalized)) {
            return true;
        }
        return aliases.contains(normalized);
    }

    private static String normalizeName(String value) {
        String normalized = value.trim();
        while (normalized.startsWith("-")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }
}
