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

## PoC Design Review (Revisited)

### Individual Review

**Good decisions:**
- The original separation between `Argument<T>` and `Command` made it easier to extend functionality without rewriting the system.
- Using `CommandResult` simplified access to parsed values and reduced casting errors.

**Less-good decisions:**
- Initial design assumed all arguments were required, making it harder to later introduce optional/default arguments.
- Alias and flag behavior were not considered early, requiring additional logic to be added to `CommandArgument`.

---

### Team Review

- The extension to support flags and aliases required changes to both `Command` and `CommandArgument`, indicating tighter coupling than initially expected.
- Handling implicit values (flags with no value) introduced ambiguity that required careful design decisions.
- Subcommand support (`dispatch`) required modifying parsing flow to treat the first positional argument differently.
- There is still some duplication between scenario logic and command parsing, which could be improved with deeper integration.

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
- This allowed the Command system to focus only on structure and flow.
- Validation logic (type checking, boolean parsing, etc.) is cleanly encapsulated.

### Command System → Argument System

- The Command system extends the Argument system by adding:
    - structure (positional vs named)
    - control flow (subcommands)
    - usability features (defaults, flags, aliases)
- Some improvements could be made:
    - support for richer validation (ranges, choices) directly in Argument classes
    - better error typing instead of generic RuntimeExceptions

---

## Future Improvements

- Add support for:
    - enum-based arguments directly in the Argument system
    - regex-based validation integrated into Argument classes
- Improve API ergonomics:
    - reduce boilerplate when defining commands
    - introduce builder-style command creation
- Improve error handling:
    - use custom exception types instead of RuntimeException
- Reduce duplication between scenarios and command definitions