### Cats with Scala

- `Working with type classes in Scala` means `working with implicit values and implicit parameters`
- There are three important components to the type class pattern:
  - `type class`
  - `instances` for particular types
  - `methods` that use `type classes`.
- `the components of type classes`:
  - traits: `type classes`
  - implicit values: type class `instances`
  - implicit parameters: type class `use`:
    - any functionality that requires a `type class instance` to work. In Scala this means any `method` that accepts instances of the type class as implicit parameters.
  - implicit classes: optional utilities that make type classes easier to use
- `implicit scope`:
  - when given `Json.toJson("A string!")` compiler searches:
    - `local or inherited definitions`
    - `imported definitions`
    - `definitions in the companion object of the type class or the parameter type (in this case JsonWriter(Type Class) or String(Parameter Type)).`
