# Reflection

## Why is this important?

Reflection allows us to examine classes during runtime, which is super powerful. It's often hard to come up with
use cases in my daily work, but when I need it, it is **extremely** powerful. You can see methods, return types,
constructors, parameters, parameter types and so much more. 

It is *expensive* to run so only use it if you need it!

## Setup

To run, `./gradlew :reflection:run`

There is a main method that then calls out to various classes and functions.

| Class                 | Contains                                              |
|-----------------------|-------------------------------------------------------|
| JavaReflection        | Very simple use of java reflection in Kotlin          |
| BasicKotlinReflection | Functions containing examples of reflection in Kotlin |
| MyCoolDataClass       | Simple Data Class to experiment with                  |