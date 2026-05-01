# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

- The Argument system uses a polymorphic `Argument<T>` abstraction with minimal overhead. `Function<String, T>` parser and a `List<Validator<T>>` are the only fields.
- No specific data types are hardcoded in `Argument<T>` itself, keeping the abstraction type-agnostic.
- Numeric range validation is handled by `NumericArgument<N extends Comparable<N>>`, which wraps `Argument<N>` and adds `inRange(min, max)`, using `Comparable` which keeps the logic identical for both `Integer` and `Double`.
- Validation predicates are attached directly to the `Argument<T>` via chained `.validate()` calls rather than implemented as a separate validation layer. This keeps parsing and validation in the same place and follows "parse, don't validate".
- `enumChoice()` allows string-choice constraints to be defined inline without creating a dedicated class per use case, following the factory method.
- `.validate(Predicate<T>, String)` appends a custom validator to a new `Argument<T>` instance, following the decorator pattern and keeping the original instance untouched.
- The library uses a custom checked exception `ArgumentException` is used instead of `RuntimeException`. Scenarios catch `ArgumentException` and re-throw as `RuntimeException`, standardizing errors internally while maintaining that failures originate from the scenario layer.
- The `Argument` system is not privy to of argument names, positions, flags, or command structure. Its only responsibility is `String -> Parsed Value`. Names, defaults, and structure are entirely the Command system's concern.

## PoC Design Analysis

### Individual Review (Argument Lead) Tony Luo

Good decisions:
- `.validate()` in the template class returns a new `Argument<T>` instance with its appended list of validators, following the decorator design pattern. This allows the parser to be used irrespective of constraints/preconditions.
- The `enumChoice()` helper function uses the factory design pattern to let the user define string-choice constraints in the moment without having to create a specific class like `DifficultyArgument`, for example.

Bad decisions:
- Storing validators in the `Argument<T>` class has the aforementioned benefits, but it sacrifices the capacity to validate arguments against each other (e.g. checking that `left < right`).
- `Argument<T>` inherently has no concept of default values, requiring scenarios to run positional count checks before parsing rather than handling the missing-argument case within the abstraction itself.

### Individual Review (Command Lead) Mohammed Ali

Good decisions:
- The separation between `Argument<T>` and `Command` allows the system to be extended (e.g., flags, defaults, subcommands) without rewriting core parsing logic. This modular design improves maintainability and extensibility.

Bad decisions:
- The decision to build commands incrementally using `addPositionalArgument` and `addNamedArgument` increases the risk of invalid intermediate states.

### Team Review

#### Design Disagreement

There is disagreement on whether default values and flag behavior should be handled within the Argument system or the Command system.
- One approach keeps the Argument system simple and places defaults in Command.
- Another approach integrates defaults into Argument for consistency across use cases.

Each approach has tradeoffs in simplicity versus consistency.

#### Design Concern

Subcommand support (e.g., `dispatch static 1`) introduces additional complexity into the Command system by requiring special handling of positional arguments. It is unclear whether subcommands should be part of the core Command abstraction or handled by a separate dispatcher layer, and there is no clear best solution at this stage.
