package se09.user.service

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("se09.user.service")
                .mainClass(Application.javaClass)
                .start()
    }
}