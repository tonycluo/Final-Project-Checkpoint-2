# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

-The Argument System uses polymorphic abstraction to enable the library to function in a data-type-agnostic manner.
-The Argument<T> template class with Function<String, T> parser and List<Validator<T>> validators lets the library handle data-type-agnostic parsing and constraint checking in one step at the beginning.
-No specific data types are hardcoded in the template, only in relevant methods.

## PoC Design Analysis

### Individual Review (Argument Lead) Tony Luo

Good decisions:
- .validate() in the template class returns a new Argument<T> instance with its appended list of validators, following the "decorator" design pattern. This allows the parser to be used irrespective of constraints/preconditions.
- The enumChoice() helper function uses the factory design pattern to let the user define string-choice constraints in the moment without having to create a specific class like DifficultyArgument, for example.

Bad decisions:
- Storing validators in the argument<T> class has the aforementioned benefits, but it sacrifices the capacity to validate the arguments against each other.
- The argument<T> class inherently doesn't have a default value, requiring the scenarios to run positional count checks before parsing.

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
