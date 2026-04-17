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
- The separation between `Argument<T>` and `Command` allows the system to be extended (e.g., flags, defaults, subcommands) without rewriting core parsing logic. This modular design improves maintainability and extensibility.

Bad decisions:
- The decision to build commands incrementally using `addPositionalArgument` and `addNamedArgument` increases the risk of invalid intermediate states.

### Team Review
Design Disagreement
There is disagreement on whether default values and flag behavior should be handled within the Argument system or the Command system.
One approach keeps the Argument system simple and places defaults in Command.
Another approach integrates defaults into Argument for consistency across use cases.
Each approach has tradeoffs in simplicity versus consistency.
Design Concern
Subcommand support (e.g., `dispatch static 1`) introduces additional complexity into the Command system by requiring special handling of positional arguments.
It is unclear whether subcommands should be part of the core Command abstraction or handled by a separate dispatcher layer, and there is no clear best solution at this stage.

