import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

// https://kotlinlang.org/docs/cancellation-and-timeouts.html
class CancellingCoroutineTests {

    /**
     * Great example of cancelling a coroutine...
     * It runs, then cancels a little bit into the routine
     * Then we join the job (Can think of it as a thread) but it was cancelled, so we just move on!
     */
    @Test
    fun `I can cancel and rejoin a coroutine`() {
        runBlocking {
            val job = launch {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancel() // cancels the job
            job.join() // waits for job's completion
            println("main: Now I can quit.")
        }
    }

    @Test
    fun `If I cancel a coroutine during a computation, it will cancel while in a FOR loop`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            val job = launch {
                // The for loop doesn't eat memory, so we can cancel while this is running
                for (i in 1..5) {
                    println("Appending $i")
                    stringBuilder.append(i)
                    delay(500L)
                }
            }

            delay(1300L)
            job.cancelAndJoin() // will cancel and then rejoin all in 1

            assertEquals(stringBuilder.toString(), "123")
        }
    }

    // This example shows a while loop, but the delay function allows the coroutine to check and see the cancellation
    @Test
    fun `If I cancel a coroutine during a computation, it won't cancel while in a WHILE loop`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            val job = launch {
                // The while loop eats memory, but the delay function allows the coroutine to check for cancellation
                var i = 0
                while (i < 5) {
                    println("Appending $i")
                    stringBuilder.append(i)
                    i++
                    delay(500L)
                }
            }

            delay(1300L)
            job.cancelAndJoin() // will cancel and then rejoin all in 1

            assertEquals("012", stringBuilder.toString())
        }
    }

    // This example shows the while loop finishing, because the computation never allows the coroutine to check for
    // the cancellation!
    @Test
    fun `If I cancel a coroutine during a computation, it will cancel while in a WHILE loop`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            // So we don't have to use delay
            val startTime = System.currentTimeMillis()
            val job = launch {
                var nextPrintTime = startTime

                // The while loop eats memory, but the delay function allows the coroutine to check for cancellation
                var i = 0
                while (i < 5) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("Appending $i")
                        stringBuilder.append(i++)
                        nextPrintTime += 500L
                    }
                }
            }

            delay(1300L)
            job.cancelAndJoin() // will cancel and then rejoin all in 1

            assertEquals("01234", stringBuilder.toString())
        }
    }

    @Test
    fun `I can make my coroutine cancellable by using the isActive property`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            // So we don't have to use delay
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime

                // The while loop eats memory, but the delay function allows the coroutine to check for cancellation
                var i = 0
                // This property will check if the coroutine scope is active or not
                while (isActive) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("Appending $i")
                        stringBuilder.append(i++)
                        nextPrintTime += 500L
                    }
                }
            }

            delay(1300L)
            job.cancelAndJoin() // will cancel and then rejoin all in 1

            assertEquals("012", stringBuilder.toString())
        }
    }

    @Test
    fun `Using finally is a nice way to exit a coroutine`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            val job = launch {
                try {
                    repeat(1000) {
                        stringBuilder.append(it)
                        delay(500L)
                    }
                }
                // When the coroutine is cancelled we will enter this finally block
                finally {
                    stringBuilder.append("finally")
                }
            }
            delay(1300L)

            job.cancelAndJoin()

            assertEquals("012finally", stringBuilder.toString())
        }
    }

    @Test
    fun `I can create non cancellable portions of coroutines`() {
        val stringBuilder = StringBuilder()

        runBlocking {
            val job = launch {
                try {
                    repeat(1000) {
                        stringBuilder.append(it)
                        delay(500L)
                    }
                }
                // When the coroutine is cancelled we will enter this finally block
                finally {
                    // This can't be cancelled out of!
                    withContext(NonCancellable) {
                        stringBuilder.append("finallyNonCancellable")
                    }
                }
            }
            delay(1300L)

            job.cancelAndJoin()

            assertEquals("012finallyNonCancellable", stringBuilder.toString())
        }
    }

    /**
     * This shows how to use a timeout with a coroutine. An exception is thrown so in theory, you can:
     *
     * Have a nested coroutine, timeout on the innermost coroutine, catch the exception on an outerlayer
     * and try and continue that way!
     */
    @Test
    fun `I can timeout of a coroutine`() {
        assertThrows<TimeoutCancellationException> {
            runBlocking {
                withTimeout(2000L) {
                    repeat(1000) {
                        println("Printing $it")
                        delay(500L)
                    }
                }
            }
        }
    }

}
