import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

// Flows represent values that are computed asynchronously
class FlowTests {

    fun basicFlow(): Flow<Int> =

        // This flow function is the builder for flows
        flow {
            println("Starting flow...")
            delay(10L)
            for (i in 1..5) {
                // This emit function is how to send values out of the flow
                emit(i)
                delay(100L)
            }
        }

    @Test
    fun `I can create a simple flow`() {
        runBlocking {
            launch {
                for (i in 1..5) {
                    delay(100L)
                    println("I can still run!")
                }
            }

            // We can collect values from a flow with collect
            basicFlow().collect { emittedVal -> println("Emitted from my flow is $emittedVal") }


        }
    }

    @Test
    fun `I can prove flows are cold and dont run unless I start collecting`() {
        runBlocking {
            println("Calling my flow function")
            basicFlow()
            println("Collecting my flow")
            basicFlow().collect { emittedVal -> println("Emitted from my flow is $emittedVal") }
            println("Collecting my flow again")
            basicFlow().collect { emittedVal -> println("Emitted from my flow is $emittedVal") }
        }
    }

}