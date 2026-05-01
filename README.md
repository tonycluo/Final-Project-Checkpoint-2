# Argument & Command Parsing Library

## Overview

This project implements a reusable Argument and Command parsing library designed to handle structured input in a type-safe, extensible, and modular way.

The system is split into two primary components:

- **Argument System**: Responsible for parsing a single `String` input into a typed value (`String -> Parsed Value`)
- **Command System**: Responsible for parsing command structure (positional arguments, named arguments, flags, aliases, defaults, and subcommands)

This separation ensures a clear division of responsibilities and allows each system to evolve independently.

---

## Project Structure
src/main/java/oop/project/library/
├── argument/ # Argument parsing system
├── command/ # Command parsing system
├── scenarios/ # Example scenarios demonstrating usage

---

## How to Use

Commands are defined declaratively using the Command API:

```java
Command command = new Command("search")
    .addPositionalArgument(CommandArgument.positional("term", Arguments.string()))
    .addNamedArgument(
        CommandArgument.named("case-insensitive", Arguments.bool())
            .alias("i")
            .optional(false)
            .implicit(true)
    );

CommandResult result = command.parse(input);
String term = result.getString("term");
boolean flag = result.getBoolean("case-insensitive");
```

---

## Features

# Argument System
- Polymorphic abstraction using Argument<T>
- No hardcoded types in the core abstraction
- Supports:
    integers
    doubles
    booleans
    strings
- Validation is attached directly to parsing via .validate(...)
- Numeric range validation supports both integers and decimals using Comparable
- Supports custom validation logic without requiring specialized classes
- Does not depend on argument names, positions, or command structure

# Command System
- Positional arguments (mul 1 2)
- Named arguments (--left 1.0)
- Argument aliases (-i)
- Optional arguments with default values (echo)
- Flag-style arguments (--flag → implicit value)
- Subcommands (dispatch static 1)
- Eager validation of command structure:
- duplicate argument names
- duplicate aliases
- Type-safe extraction of parsed values via CommandResult

---

## Feature Showcase
A custom feature was implemented to generate command usage strings automatically.

```
Command command = new Command("search")
    .addPositionalArgument(CommandArgument.positional("term", Arguments.string()))
    .addNamedArgument(
        CommandArgument.named("case-insensitive", Arguments.bool())
            .alias("i")
            .optional(false)
            .implicit(true)
    );

System.out.println(command.usage());
```
Output:
```search <term> [--case-insensitive|-i]```

This feature demonstrates that command definitions can describe themselves without manual documentation, improving usability and debugging beyond the assignment requirements.

## Design Highlights
- Strong separation of concerns between Argument and Command systems
- Argument system focuses strictly on String -> Parsed Value
- Command system handles structure, defaults, aliases, and control flow
- Validation is composable and reusable through the Argument abstraction
- Type-safe extraction prevents runtime casting errors
- Minimal API overhead for simple use cases

## Error Handling
- The library uses custom exceptions (e.g., CommandParseException, ArgumentException) for internal error handling
- Scenarios catch these exceptions and rethrow RuntimeException to match expected behavior
- Error messages are descriptive and indicate the cause of failure

---

## Notes
- The Command system relies on the provided Input parser for tokenization
- Scenarios demonstrate proper usage of the library, including error handling and typed extraction
- The design prioritizes:
    modularity
    extensibility
    type safety
