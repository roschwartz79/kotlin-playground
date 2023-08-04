import JavaReflection.JavaReflection
import KotlinReflection.BasicKotlinReflection
import ObjectClasses.MyCoolDataClass

fun main(args: Array<String>) {

    val javaReflection = JavaReflection()
    val basicKotlinReflection = BasicKotlinReflection()

    // Inspect a data class using Java reflection
//    javaReflection.useJavaReflection()

    // Now to use the Kclass object!
//    basicKotlinReflection.createKClass()
//    basicKotlinReflection.accessProperties()
//    basicKotlinReflection.createInstance()

    val created = basicKotlinReflection.createInstanceTheHardWay(MyCoolDataClass::class)
    println("CREATED INSTANCE: $created")
}