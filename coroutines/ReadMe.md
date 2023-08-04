# Coroutines

## Why is this important?

Coroutines allow us to perform asynchronous activities in our applications. It is similar to threading in Java, however 
it's a massive improvement! It's super lightweight and has lots of guardrails in place so you don't exceed your memory 
threshold. 

*Note* An important distinction with coroutines is that they share Thread Pools, otherwise known as dispatchers. A given
coroutine may start on a specific thread (off the main thread), pause, and then be picked up again on a different 
thread! There are also different groups of thread pools, or dispatchers!

## How to run

`./gradlew clean :coroutines:test`

## Test Classes

I've organized examples into test cases to keep them simple, in the future I would like to create a runnable application
but this was the best way I could try lots of things out in an organized way!

| Class                    | Contains                                              |
|--------------------------|-------------------------------------------------------|
| BasicCoroutineTests      | Basic fundamentals of Coroutines                      |
| CancellingCoroutineTests | Cancelling, Exceptions and Timeouts within Coroutines |
| CoroutineDispatcherTests | Exploring various dispatchers                         |
| SuspendFunctionTests     | Creating Suspend functions in practice                |