package KotlinReflection

import ObjectClasses.MyCoolDataClass
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

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

    // Multiple ways we can create instances
    fun createInstance() {
        val clazz = MyCoolDataClass::class

        val createdInstance = clazz.createInstance()

        println("First created instance: $createdInstance")

        val simpleKClass: KClass<MyCoolDataClass> = MyCoolDataClass::class

        val kclassCreatedInstance = simpleKClass.createInstance()

        println("Kclass created instance: $kclassCreatedInstance")
    }

    fun <T : Any> createInstanceTheHardWay(clazz: T): T {

        val objectAsKClass: KClass<out T> = clazz::class

        // This has nothing to do with the return object of this method- simply here for visibility
        objectAsKClass.declaredMemberProperties.forEach {
            println("Member Property name: ${it.name} and its type: ${it.returnType}")
        }

        val newClass = objectAsKClass.createInstance()

        return newClass

    }

}