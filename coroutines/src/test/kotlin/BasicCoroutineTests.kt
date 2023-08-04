import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class BasicCoroutineTests {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    @Test
    fun `test simple runBlocking`() {
        var testVal = 0

        // If there is a delay, the runblocking section will block the current thread until it's complete
        // This bridges main code with any suspending code
        runBlocking {
            assertEquals(0, testVal)
            delay(5)
            testVal = 1
        }

        assertEquals(1, testVal)
    }

    @Test
    fun `test simple launch`() {

        val stringBuilder = StringBuilder()

        runBlocking {
            launch { // launch a new coroutine and continue
                delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                stringBuilder.append("hello")
            }

            stringBuilder.append("world")

        }

        assertEquals("worldhello", stringBuilder.toString())
    }

    @Test
    fun `I can use suspend functions in my coroutine scope`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            opera(stringBuilder)
            stringBuilder.append("mi")
        }

        assertEquals("faredomi", stringBuilder.toString())

    }

    // Suspend functions can be called from coroutines
    private suspend fun opera(stringBuilder: StringBuilder): StringBuilder = coroutineScope { // this: CoroutineScope
        launch {
            delay(2000L)
            stringBuilder.append("do")
        }
        launch {
            delay(1000L)
            stringBuilder.append("re")
        }
        stringBuilder.append("fa")
    }

    @Test
    fun `I can get a job back and use join it`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            // Inside a coroutine scope, we can store the "job" in a val and connect to it to join back into that "thread"
            val job = launch {
                delay(1000L)
                stringBuilder.append("hello")
            }
            stringBuilder.append("my")
            job.join() // wait until child coroutine completes
            stringBuilder.append("friend")
        }

        assertEquals("myhellofriend", stringBuilder.toString())
    }

    @Test
    fun `I can use async to return a value`() {
        val stringBuilder = StringBuilder()
        runBlocking {
            // whereas launch returns a job, async returns a "Deferred" wrapper object
            val deferred: Deferred<String> = async {
                delay(1000L)
                stringBuilder.append("Hello")
                return@async stringBuilder.toString()
            }

            assertEquals(deferred.await(), "Hello")
        }
    }

    // Got this from the kotlin docs!
    @Test
    fun `I can awaitAll on an async coroutine scope`() {
        runBlocking {
            // I can defer the results to a list!
            val deferreds: List<Deferred<Int>> = (1..3).map {
                async {
                    delay(1000L * it)
                    println("Loading $it")
                    it
                }
            }
            // Notice awaitAll versus await here!
            val sum = deferreds.awaitAll().sum()
            println("$sum")
        }
    }

    @Test
    fun `I can start scopes from the Global Scope`() {

        runBlocking {
            val string = globalScope(StringBuilder())
            assertEquals(string, "Hello")
        }
    }

    private suspend fun globalScope(stringBuilder: StringBuilder): String {
        val string = GlobalScope.async {
            delay(1000L)
            stringBuilder.append("Hello")
            return@async stringBuilder.toString()
        }

        return string.await()
    }

    @Test
    fun `test async in runBlocking will work in parallel`() {

        val time = measureTimeMillis {
            runBlocking {
                (0..99).map { number ->
                    println("$number started")
                    val deferred = async {
                        delay(100)
                        println("$number run")
                        true
                    }
                    deferred
                }.forEach { it.await() }
            }
        }
        assert(time < 500)
    }
}