package se09.user.service.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/health")
class HealthController {

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun health(): Map<String, String> {
        return mapOf("status" to "running")
    }
}
