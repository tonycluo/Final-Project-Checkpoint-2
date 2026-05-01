# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

- The Argument system uses a polymorphic `Argument<T>` interface with minimal overhead. Each concrete type (`IntegerArgument`, `DoubleArgument`, `BooleanArgument`, `StringArgument`) has shared fields or superclass coupling.
- No specific data types are hardcoded in `Argument<T>` itself, keeping the abstraction type-agnostic. Types only appear in their concrete implementations and in the `Arguments` factory.
- Numeric range validation is supported directly on `IntegerArgument` and `DoubleArgument` via `inRange(min, max)`, which returns a new constrained instance. The same pattern works for both types without duplicating logic.
- String choice validation is built into `StringArgument` via `.choices(String...)`, supporting case-insensitive matching and returning the value in lowercase. Pattern-based validation is available via `.regex(String)`.
- The library uses `ArgumentParseException` (extending `RuntimeException`) instead of generic `RuntimeException` for internal errors. This gives callers a specific type to catch at the boundary while remaining unchecked, so it doesn't break existing `catch (RuntimeException)` blocks in the Command system.
- The `Argument` system has no knowledge of argument names, positions, flags, or command structure. Its only responsibility is `String -> Parsed Value`. Names, defaults, and structure are entirely the Command system's concern.

## PoC Design Analysis

### Individual Review (Argument Lead) Tony Luo

Good decisions:
- `StringArgument.choices()` and `inRange()` on numeric types follow the decorator pattern — each returns a new constrained instance, leaving the base argument reusable. This allows `Arguments.integer()` to be used as-is or extended per scenario without side effects.
- The `Arguments` factory class lets callers define argument types inline (e.g. `Arguments.string().choices("easy", "normal", "hard")`) without creating a dedicated class per use case, keeping the API minimal for simple cases.

Bad decisions:
- `Argument<T>` has no concept of default values, requiring scenarios to run positional count checks before parsing rather than handling the missing-argument case within the abstraction itself.
- Validation is per-argument only — there is no way to validate arguments against each other (e.g. checking that `left < right`) within this system.

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
