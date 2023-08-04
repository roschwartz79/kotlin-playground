package JavaReflection

import ObjectClasses.MyCoolDataClass

class JavaReflection {

    fun useJavaReflection() {
        MyCoolDataClass::class.java.methods.forEach {
            println("Method: $it")
        }
    }
}