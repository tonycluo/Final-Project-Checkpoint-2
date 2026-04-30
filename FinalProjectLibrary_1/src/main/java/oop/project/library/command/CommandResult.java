package oop.project.library.command;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CommandResult {

    private final String commandName;
    private final Map<String, Object> values;

    public CommandResult(String commandName, Map<String, Object> values) {
        this.commandName = Objects.requireNonNull(commandName, "commandName");
        this.values = Collections.unmodifiableMap(
                new LinkedHashMap<>(Objects.requireNonNull(values, "values"))
        );
    }

    public String getCommandName() {
        return commandName;
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public Object get(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No argument named '" + name + "'.");
        }
        return values.get(name);
    }

    /**
     * Retrieves a parsed argument by name and verifies its expected type.
     *
     * This is the main typed extraction method used by scenarios after parsing.
     * Incorrect usage results in clear errors from the library rather than unsafe casts.
     *
     * @param name the parsed argument name
     * @param type the expected Java type
     * @return the parsed value as the requested type
     * @param <T> the expected value type
     * @throws IllegalArgumentException if no parsed argument exists with the given name
     * @throws IllegalStateException if the parsed value does not match the expected type
     */
    public <T> T get(String name, Class<T> type) {
        Objects.requireNonNull(type, "type");
        Object value = get(name);

        if (!type.isInstance(value)) {
            throw new IllegalStateException(
                    "Argument '" + name + "' is of type " +
                            value.getClass().getSimpleName() +
                            ", not " + type.getSimpleName() + "."
            );
        }

        return type.cast(value);
    }

    public Integer getInt(String name) {
        return get(name, Integer.class);
    }

    public Double getDouble(String name) {
        return get(name, Double.class);
    }

    public Boolean getBoolean(String name) {
        return get(name, Boolean.class);
    }

    public String getString(String name) {
        return get(name, String.class);
    }

    public Map<String, Object> asMap() {
        return values;
    }
}
