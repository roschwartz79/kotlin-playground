import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class ChannelTests {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    @Test
    fun `simple channel example`() {
        runBlocking {
            val channel = Channel<Int>()
            launch {
                // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
                for (x in 1..5) {
                    sleep(1000L*x)
                    channel.send(x * x)
                }
            }

            repeat(5) { println(channel.receive()) }
            println("Done!")
        }
    }

    @Test
    fun `simple channel with a close`() {
        runBlocking {
            val string = "hello"
            val stringBuilder = StringBuilder()
            val channel = Channel<String>()
            launch {
                for (x in string) channel.send("$x$x")
                channel.close()
            }

            // We can iterate over each of the values emmitted until the channel is closed!
            for (str in channel) stringBuilder.append(str)
            println("Done!")

            assertEquals("hheelllloo", stringBuilder.toString())
        }
    }

    // This was taken right from the kotlin docs
    @Test
    fun `Example of a pipeline`() {
        runBlocking {
            val numbers = produceNumbers() // produces integers from 1 and on
            val squares = square(numbers) // squares integers
            repeat(5) {
                println(squares.receive()) // print first five
            }
            println("Done!") // we are done
            coroutineContext.cancelChildren() // cancel children coroutines
        }
    }

    // A producer that produces a number infinitely
    private fun CoroutineScope.produceNumbers() = produce<Int> {
        var x = 1
        while (true) send(x++) // infinite stream of integers starting from 1
    }

    // A producer that consumes a channel, does some work, then produces it
    private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) send(x * x)
    }

    @Test
    fun `Produce numbers divisible by 5`() {
        runBlocking {
            val start = 3
            var numbers = produceNumbersPt2(start)

            repeat(18) {
                numbers = filterNumsDivisibleBy5(numbers)
                println(numbers.receive())
            }
            println("Done!")
            coroutineContext.cancelChildren() // cancel children coroutines
        }
    }

    private fun CoroutineScope.produceNumbersPt2(start: Int) = produce {
        var x = start
        while (true) send(x++)
    }

    private fun CoroutineScope.filterNumsDivisibleBy5(numbers: ReceiveChannel<Int>) = produce {
        for (x in numbers) if (x % 5 == 0) send(x)
    }

    // This is actually the coolest feature in my opinion, we can set how much "work" we want to do and let it build up
    // until we decide, you know what there's too much in the queue lets suspend.
    @Test
    fun `I can use channels with buffers!`() {
        runBlocking {
            val channel = Channel<Int>(7) // create buffered channel
            val sender = launch {
                repeat(10) {
                    println("Sending $it")
                    channel.send(it) // will suspend when buffer is full (7 elements)
                }
            }
            // don't receive anything... just wait....
            // when we receive we will get everything in the buffer!
            delay(1000)
            sender.cancel()
        }
    }
}