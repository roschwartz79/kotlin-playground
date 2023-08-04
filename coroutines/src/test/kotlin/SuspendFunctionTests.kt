import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.invoke.StringConcatException
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.text.StringBuilder

class SuspendFunctionTests {

    // We will use these 2 suspend functions in our tests
    private suspend fun appendHello(): String {
        delay(1000L)

        return StringBuilder("hello").toString()
    }

    private suspend fun appendWorld(): String {
        delay(1000L)

        return StringBuilder("world").toString()
    }

    // Example of using structured concurrency within coroutines and suspend functions
    private suspend fun appendHelloWorld() = coroutineScope {
        val hello = async { appendHello() }
        val world = async { appendWorld() }

        hello.await() + world.await()
    }

    // Using structured concurrency to control cancelling all coroutines in a scope if one bombs
    private suspend fun coroutineThrowsError() = coroutineScope {
        val hello = async {
            try {
                delay(50000L)
                appendHello()
            } finally {
                println("Append Hello was cancelled")
            }
        }
        val world = async {
            println("Append World throws an exception")
            throw StringConcatException("Oops")
        }

        hello.await() + world.await()
    }

    @Test
    fun `I can create a suspend function`() {
        runBlocking {
            val stringBuilder = StringBuilder()

            val time = measureTimeMillis {
                stringBuilder.append(appendHello())
                stringBuilder.append(appendWorld())
            }

            assertEquals("helloworld", stringBuilder.toString())
            println("Time running sequentially: $time ms")
        }
    }

    // This is showing how we can run suspend functions in parallel (keeping their inputs and return vars separated)
    // Notice the time to complete is half of what the test right above is!
    @Test
    fun `Running suspend functions asynchronously`() {
        runBlocking {
            val stringBuilder1 = StringBuilder()
            val stringBuilder2 = StringBuilder()

            val time = measureTimeMillis {
                val helloString = async { stringBuilder1.append(appendHello()) }
                val worldString = async { stringBuilder2.append(appendWorld()) }

                val composedString = helloString.await().toString() + worldString.await().toString()

                assertEquals("helloworld", composedString)
            }

            println("Time running asynchronously: $time ms")
        }
    }

    // The same as above, however the coroutine only starts if required with either .await() OR starting the job
    @Test
    fun `Running suspend functions asynchronously, but starting the async coroutine lazily`() {
        runBlocking {
            val stringBuilder1 = StringBuilder()
            val stringBuilder2 = StringBuilder()

            val time = measureTimeMillis {
                val helloString = async(start = CoroutineStart.LAZY) { stringBuilder1.append(appendHello()) }
                val worldString = async(start = CoroutineStart.LAZY) { stringBuilder2.append(appendWorld()) }

                val composedString = helloString.await().toString() + worldString.await().toString()

                assertEquals("helloworld", composedString)
            }

            println("Time running asynchronously, starting lazily: $time ms")
        }
    }

    // The same as above, however the coroutine only starts if required with either .await() OR starting the job
    @Test
    fun `A lazy coroutine doesn't run without a job starting it or awaiting for the value`() {
        runBlocking {
            val stringBuilder1 = StringBuilder()
            val stringBuilder2 = StringBuilder()

            val time = measureTimeMillis {
                val helloString = async(start = CoroutineStart.LAZY) { stringBuilder1.append(appendHello()) }
                val worldString = async(start = CoroutineStart.LAZY) { stringBuilder2.append(appendWorld()) }

                // Run the hello async coroutine
                helloString.start()

                assertEquals("hello", helloString.await().toString())
                assertNotEquals("world", worldString.toString()) // The world coroutine hasn't been started!

                worldString.start()

                assertEquals("world", worldString.await().toString())
            }

            println("Time running asynchronously, manually starting each coroutine: $time ms")
        }
    }

    @Test
    fun `I can use structured concurrency with suspend functions`() {
        runBlocking {
            val time = measureTimeMillis {
                val helloWorld = appendHelloWorld()
                assertEquals("helloworld", helloWorld)
            }

            println("Time running asychronously with structured concurrency is $time ms")
        }
    }

    /**
     * This is a great example showing how we can use structured concurrency
     * If we structure our coroutines properly, we can cancel out of all the coroutines if there is an error
     * while working through one of them.
     *
     * This is much better than letting 1 process and 1 fail and continuing, this keeps everything related together
     */

    @Test
    fun `Using structured concurrency I can control cancellation of all coroutines`() {
        runBlocking {
            try {
                coroutineThrowsError()
            } catch (e: StringConcatException) {
                println("Caught a StringConcatException!")
            }
        }
    }
}