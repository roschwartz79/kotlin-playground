package KotlinReflection

import ObjectClasses.MyCoolDataClass
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.*

class BasicKotlinReflection {

    fun createKClass() {
        val simpleKClass: KClass<MyCoolDataClass> = MyCoolDataClass::class

        println("Is MyCoolDataClass a data class? ${simpleKClass.isData}")
    }

    fun accessProperties() {
        val simpleKClass: KClass<MyCoolDataClass> = MyCoolDataClass::class

        simpleKClass.declaredMemberProperties.forEach {
            println("Member Property name: ${it.name} and its type: ${it.returnType}")
        }

        // We can also access properties like this
        val name = MyCoolDataClass::myName

        println("The name is ${name}")
    }

    // Multiple ways we can create instances using the zero arg constructor
    fun createInstance() {
        val clazz = MyCoolDataClass::class

        val createdInstance = clazz.createInstance()

        println("First created instance: $createdInstance")

        val simpleKClass: KClass<MyCoolDataClass> = MyCoolDataClass::class

        val kclassCreatedInstance = simpleKClass.createInstance()

        println("Kclass created instance: $kclassCreatedInstance")
    }

    // I can create an instance using the constructor
    fun createInstanceWithConstructor() {
        val clazz = MyCoolDataClass::class

        val primaryConstructor = clazz.primaryConstructor
        val newClass = primaryConstructor!!.call("first arg", 20, false)

        print("Created the instance with the contructor: $newClass")
    }

    // I can create an instance of ANY class (That only has strings, ints and booleans :) )
    fun <T : Any> createInstanceTheHardWay(clazz: KClass<T>): T {

        // Map we can store our generated values for the constructor in
        val argsMap: HashMap<KParameter, Any> = HashMap()

        // Getting the primary constructor of any given class
        // From this we can get all the information on how to create a class
        val primaryConstructor = clazz.primaryConstructor

        // How can we get the params and generate random ones
        primaryConstructor?.parameters?.forEach { param ->
            println("Index is ${param.index} and the type is ${param.type}")
            when (param.type) {
                // create type turns a KClass into a type so we can compare classes to param types
                Int::class.createType() -> {
                    println("I'm an Int!")
                    argsMap[param] = intGenerator()
                }

                String::class.createType() -> {
                    println("I'm a String!")
                    argsMap[param] = stringGenerator()
                }

                else -> {
                    println("I'm a Bool!")
                    argsMap[param] = booleanGenerator()
                }
            }
        }

//        return primaryConstructor!!.call(argsMap[0], argsMap[1], argsMap[2]) // If we wanted to give the args one at a time

        return primaryConstructor!!.callBy(argsMap)

    }

    fun intGenerator(): Int {
        return Random.nextInt(2, 100)
    }

    fun stringGenerator(): String {
        return Random.nextDouble().toString()
    }

    fun booleanGenerator(): Boolean {
        return Random.nextBoolean()
    }

}