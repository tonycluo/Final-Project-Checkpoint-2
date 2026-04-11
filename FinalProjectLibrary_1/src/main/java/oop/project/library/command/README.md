# Argument System

Handles creation of creation command structures and multi-argument parsing.

## Development Notes

- Designed a 'Command' class to handle parsing of both positional and named arguments using a unified structure.
- Used a CommandArgument<T> abstraction to associate argument names with their parsing logic.
- Introduced CommandResult to provide type-safe access to parsed values.
- Implemented support for positional arguments (mul 1 2) and named arguments (div --left 1 --right 2).
- Focused on validating inputs early (missing arguments, extra types, etc.)

## PoC Design Analysis

### Individual Review (Command Lead) MOHAMMED ALI
Good decisions:
- Separating 'Argument<T>' from 'Command' made the system modular and reusable.
- Introducing 'CommandResult' improved usability by avoiding repeated casting from 'Map<String, Object>'.

Bad decisions:
- The initial design treated all named arguments as required, which made it harder later to support flags.
- Subcommand support was not planned early, so adding it required modifying the 'Command' structure.

### Individual Review (Argument Lead)
- Added basic Argument class skeleton for CommandArgument.java.

### Team Review
- There was uncertainty about how much logic should be placed in 'CommandScenarios' versus the reusable 'Command' system.
- Subcommands introduced additional complexity in parsing structure.
- Improvements for future work: cleaner API for optional arguments, more specific error handling, support for more complex argument types.