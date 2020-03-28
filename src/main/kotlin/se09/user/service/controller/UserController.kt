package se09.user.service.controller

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.services.UserClientService
import se09.user.service.ws.HydraService
import javax.inject.Inject
@Validated
@Controller("/users")
class UserController {

    @Inject
    private lateinit var userClientService: UserClientService

    @Inject
    private lateinit var hydraService: HydraService

    @Value("\${micronaut.application.externalhost}")
    private lateinit var externalHostname: String

    private val LOG: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Post(value = "/client", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun createUserClient(
            @Header(value = "Authorization") authHeader: String
    ): HttpResponse<Any> {
        LOG.warn("createUserClient")
        val token = authHeader.substringAfter(" ")
        val userMail = hydraService.introspectToken(token).sub
        return if (userMail != null) {
            val dto  = userClientService.createUserClient(userMail)
            HttpResponse.ok(dto)
        } else {
            HttpResponse.unauthorized()
        }
    }

}