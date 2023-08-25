import new.`package`.packageLevelSwap
fun main(args: Array<String>) {

    var list = mutableListOf(1, 2, 3, 4)
    println("List is $list")

    list.swap(0,3)

    println("After the first swap, the list is $list")

    list.genericSwap(0,3)

    println("After the second swap, the list is $list")

    list.packageLevelSwap(1, 2)

    println("After the package level swap the list is $list")

    var nullable: String? = null

    println(nullable.toStringNullCheck())

    nullable = "hi"

    println(nullable.toStringNullCheck())

}


// Simple example of an extension function
// This refers to the object that calls the function, so in this case it is the list of Ints
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

// Creating a generic swap function
fun <T> MutableList<T>.genericSwap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

// You can create extension functions that deal with nullables
fun Any?.toStringNullCheck(): String {
    if (this == null) { return "I am null!" }

    return this.toString()
}