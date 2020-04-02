package se09.user.service.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import se09.user.service.dto.MQTTRegisterDTO
import se09.user.service.services.UserClientService
import se09.user.service.utils.GlobalLogger
import javax.inject.Inject

@Controller("/mqtt")
class MQTTController {

    @Inject
    private lateinit var userClientService: UserClientService

    @Post(value = "/register", produces = [MediaType.APPLICATION_JSON])
    fun handleMQTTRegister(
            @Body body: MQTTRegisterDTO
    ): HttpResponse<Any> {
        val valid = userClientService.mqttLoginValid(body)
        GlobalLogger.info(mapOf(
                "event" to "/mqtt/register",
                "value" to valid
        ))
        return if(valid) HttpResponse.ok()
        else HttpResponse.unauthorized()
    }

}
