import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext
import kotlin.io.println
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CoroutineDispatcherTests {

    @Test
    fun `Testing various dispatchers for coroutines`() {
        runBlocking {
            println("Main runBlocking context running in:                              ${Thread.currentThread().name}")

            launch {
                println("Launch running in same context as parent dispatcher:              ${Thread.currentThread().name}")
            }

            launch(Dispatchers.Unconfined) {
                println("Launch running with Unconfined dispatcher:                        ${Thread.currentThread().name}")
            }

            launch(Dispatchers.Default) {
                println("Launch running with Default dispatcher:                           ${Thread.currentThread().name}")
            }

            launch(Dispatchers.IO) {
                println("Launch running with IO dispatcher:                                ${Thread.currentThread().name}")
            }

            launch(newSingleThreadContext("CustomThread")) {
                println("Launch running with CustomThread dispatcher:                     ${Thread.currentThread().name}")
            }

            launch(Dispatchers.Unconfined) {
                launch(Dispatchers.IO) {
                    println("Launch running with IO dispatcher within an Unconfined coroutine: ${Thread.currentThread().name}")
                }
            }
        }
    }

    @Test
    fun `Working with the unconfined dispatcher`() {
        runBlocking {
            launch(Dispatchers.Unconfined) {
                println("BEFORE delay with Unconfined Dispatcher:      ${Thread.currentThread().name}")
                delay(1000)
                println("AFTER delay with Unconfined Dispatcher:       ${Thread.currentThread().name}")
            }

            launch {
                println("BEFORE delay with runBlocking Dispatcher:     ${Thread.currentThread().name}")
                delay(1000)
                println("AFTER delay with runBlocking Dispatcher:      ${Thread.currentThread().name}")
            }
        }
    }

    @Test
    fun `I can switch threads in a single coroutine`() {
        newSingleThreadContext("MyFirstThread").use { firstCtx ->
            newSingleThreadContext("MySecondThread").use { secondCtx ->
                runBlocking(firstCtx) {
                    println("Working in thread: ${Thread.currentThread().name}")

                    // Switch threads
                    withContext(secondCtx) {
                        println("Working in thread: ${Thread.currentThread().name}")
                    }

                    // Back to the first thread!
                    println("Working in thread: ${Thread.currentThread().name}")
                }
            }
        }
    }

    // We can have children of coroutines totally separate from it's parents.
    // This is especially important for lifecycles
    @Test
    fun `I can create children that dont care about the main coroutine`() {
        runBlocking {
            val stringBuilder1 = StringBuilder()
            val stringBuilder2 = StringBuilder()
            val mainJob = launch {

                // We can launch coroutines that have their own Jobs
                // This coroutine has nothing tied to the job of the parent
                launch(Job()) {
                    delay(2000L)
                    println("Appending in my new Job.")
                    stringBuilder1.append("Hello")
                }

                // This coroutine is tied to the job of the parent
                launch {
                    delay(2000L)
                    println("Appending in the same Job as my parent.")
                    stringBuilder2.append("This will never happen :/")
                }
            }

            delay(500)
            mainJob.cancel()
            println("The parent Job has been cancelled.")
            delay(3000L)
            assertEquals("Hello", stringBuilder1.toString())
            assertNotEquals("This will never happen :/", stringBuilder1.toString())

        }
    }

    // The big benefit here is during debugging- especially if you have a lot of layers!
    @Test
    fun `I can name coroutines`() {
        runBlocking(CoroutineName("Main Runblocking")) {
            println("Top level runblocking ---> I'm in ${this.coroutineContext}")
            launch(CoroutineName("Launch Coroutine 1")) {
                println("1st nested launch ---> I'm in ${this.coroutineContext}")
                launch(CoroutineName("Launch Coroutine 2")) {
                    println("2nd Nested launch ---> I'm in ${this.coroutineContext}")
                    async(CoroutineName("Async Coroutine 1")) {
                        println("1st Nested async --->I'm in ${this.coroutineContext}")
                    }
                }
            }
        }
    }

    @Test
    fun `Combine Coroutine context elements`() {
        runBlocking(Dispatchers.IO + CoroutineName("Top level run blocking")) {
            println("This is the best coroutine ever written!")
        }
    }

    // Really cool way to control coroutines even further
    // There are 2 factory methods- MainScope() or CoroutineScope()
    //
    // NOTE: This is not the best way to use them, this is simply for a test.
    // For a good example of a use case between classes:
    // https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html#coroutine-scope
//    @Test
//    fun `I can use scopes to manage the lifecycle of my coroutines`() {
//
//        val mainScope = MainScope()
//
//        mainScope.launch {
//            println("I'm in the main scope!")
//        }
//
//         You can cancel the scope too!
//        mainScope.cancel("Because I said so!")
//    }


}