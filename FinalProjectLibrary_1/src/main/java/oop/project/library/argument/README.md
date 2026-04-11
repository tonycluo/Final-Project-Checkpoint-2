# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

-Used the Input class to tokenize raw strings into positional and named arguments with parseBasicArgs(), following “parse, don’t validate”.

-Integer parsing explicitly rejects inputs containing ‘.’ before calling Integer.parseInt, so decimal inputs fail cleanly rather than throwing a NumberFormatException, which would be more unclear.

-Part of the sub scenario represents a bug where negative decimals are rejected. The helper function parseSubDouble handles this by checking for a leading ‘-’ and a ‘.’.

-LocalDate.parse() is used for the date scenario since it defaults to ISO format (yyyy-MM-dd), which exactly matches the expected input.

-The difficulty scenario is handled using Set.of() for lookup.


## PoC Design Analysis

### Individual Review (Argument Lead) Tony Luo

Good decisions:

-Reused Input.parseBasicArgs() to tokenize inputs, which distinctly separates each scenario, keeping their functionality to validation and type conversion.

-Implemented the helper functions parseInteger and parseSubDouble due to their potential reusability in future scenarios.

Less-good decisions:

-Errors are thrown as plain RuntimeExceptions, which makes it hard for users to identify the exact error in the inputs.

-The sub-bug workaround seems a bit flimsy. A check for a lead “-” and a “.” is not elegant and doesn’t utilize data types to our advantage. 


### Individual Review (Command Lead) MOHAMMED ALI
Good decisions:
- Separating 'Argument<T>' from 'Command' made the system modular and reusable.
- Introducing 'CommandResult' improved usability by avoiding repeated casting from 'Map<String, Object>'.

Bad decisions:
- The initial design treated all named arguments as required, which made it harder later to support flags.
- Subcommand support was not planned early, so adding it required modifying the 'Command' structure.

### Team Review
There was uncertainty about how much logic should be placed in 'CommandScenarios' versus the reusable 'Command' system.
Subcommands introduced additional complexity in parsing structure.
Improvements for future work: cleaner API for optional arguments, more specific error handling, support for more complex argument types.
