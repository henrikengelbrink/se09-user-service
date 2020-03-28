package se09.user.service.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.dto.MQTTRegisterDTO
import se09.user.service.services.UserClientService
import javax.inject.Inject

@Controller("/mqtt")
class MQTTController {

    private val LOG: Logger = LoggerFactory.getLogger(MQTTController::class.java)

    @Inject
    private lateinit var userClientService: UserClientService

    @Post(value = "/register", produces = [MediaType.APPLICATION_JSON])
    fun handleMQTTRegister(
            @Body body: MQTTRegisterDTO
    ): HttpResponse<Any> {
        LOG.warn("########### handleMQTTRegister")
        val valid = userClientService.mqttLoginValid(body)
        return if(valid) HttpResponse.ok()
        else HttpResponse.unauthorized()
    }

}
