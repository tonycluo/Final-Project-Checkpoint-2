## Development Notes (Updated for MVP)

- Extended the original Command system to support additional features required for MVP.
- Maintained separation of concerns:
    - `Argument<T>` handles parsing of individual values.
    - `Command` handles parsing of full command structures.
- Updated `CommandArgument<T>` to support:
    - default values for optional arguments
    - implicit values for flags (e.g., `--case-insensitive` → true)
    - aliases (e.g., `-i` for `--case-insensitive`)
- Updated `Command` to support:
    - optional positional arguments (used in `echo`)
    - optional named arguments with defaults (used in `search`)
    - flag-style named arguments with no explicit value
    - alias resolution for named arguments
    - subcommand parsing (used in `dispatch`)
- Added logic to normalize argument names (strip leading dashes) to unify handling of `--flag` and `-f`.
- Used `CommandResult` to provide type-safe access to parsed values, while still returning `Map<String, Object>` in scenarios to match tests.
- Ensured all parsing errors are caught and rethrown with clear messages.

---

## Individual Review (Command System)

### Good Design Decisions

- The separation between `Argument<T>` and `Command` allowed the system to be extended (e.g., flags, defaults, subcommands) without rewriting core parsing logic. This modular design improves maintainability and extensibility.
- The use of `CommandResult` for typed value extraction avoids unsafe casting and enforces correct usage of parsed values, improving reliability and developer experience.

### Less-Good Design Decisions

- The original design assumed all arguments were required, which made introducing optional/default arguments more complex and required modifying existing parsing logic rather than extending it cleanly.
- The decision to build commands incrementally using `addPositionalArgument` and `addNamedArgument` increases the risk of invalid intermediate states (e.g., duplicate names), suggesting that a more constrained or immutable construction approach could improve safety.

---

## Individual Review (Argument System)

### Good Design Decision

- The Argument system uses a polymorphic abstraction (`Argument<T>`) instead of hardcoding specific types, allowing flexible support for different data types (e.g., integers, booleans, enums) without modifying existing logic.

### Less-Good Design Decision

- Validation logic is tightly coupled with parsing logic within the same abstraction, which reduces flexibility. Separating validation into a distinct layer could improve reuse and composability (e.g., applying the same validation across different argument types).

---

## Team Review

### Design Disagreement

- There is disagreement on whether default values and flag behavior should be handled within the Argument system or the Command system.
    - One approach keeps the Argument system simple and places defaults in Command.
    - Another approach integrates defaults into Argument for consistency across use cases.
- Each approach has tradeoffs in simplicity versus consistency.

### Design Concern

- Subcommand support (e.g., `dispatch static 1`) introduces additional complexity into the Command system by requiring special handling of positional arguments.
- It is unclear whether subcommands should be part of the core Command abstraction or handled by a separate dispatcher layer, and there is no clear best solution at this stage.

---

## Scenario Review (Updated)

### Structure Review

- All command scenarios follow a consistent structure:
    - Parse input using `Input(...).parseBasicArgs()`
    - Construct a `Command` object with defined arguments
    - Call `command.parse(...)`
    - Return `result.asMap()`
- Error handling is consistent:
    - Missing arguments throw exceptions
    - Extra arguments are rejected
    - Invalid types are caught during parsing
- Typed value extraction is handled through the Argument system:
    - integers, doubles, booleans, and strings are parsed via `Argument<T>`
- All scenarios match expected behavior:
    - `mul` uses positional integers
    - `div` uses named doubles
    - `echo` uses optional positional with default
    - `search` uses flags, defaults, and aliases
    - `dispatch` uses subcommands

---

## Cross Design Review (Updated)

### Argument System → Command System

- The Argument system provides a reusable abstraction for parsing values.
- This allows the Command system to focus on structure and flow rather than type conversion.
- Validation logic (type checking, boolean parsing, etc.) is cleanly encapsulated within Argument abstractions.

### Command System → Argument System

- The Command system extends the Argument system by adding:
    - structure (positional vs named arguments)
    - control flow (subcommands)
    - usability features (defaults, flags, aliases)
- Potential improvements:
    - support for richer validation (ranges, choices) directly in Argument classes
    - improved error typing instead of relying on generic RuntimeExceptions

---

## Future Improvements

- Add support for:
    - enum-based arguments directly in the Argument system
    - regex-based validation integrated into Argument classes
- Improve API ergonomics:
    - reduce boilerplate when defining commands
    - introduce builder-style or immutable command creation patterns
- Improve error handling:
    - use custom exception types instead of RuntimeException
- Reduce duplication between scenarios and command definitions
