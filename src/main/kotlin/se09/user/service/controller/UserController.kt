package se09.user.service.controller

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import se09.user.service.exceptions.APIException
import se09.user.service.exceptions.APIExceptionCode
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

    @Post(value = "/client", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun createUserClient(
            @Header(value = "Authorization") authHeader: String
    ): HttpResponse<Any> {
        val token = authHeader.substringAfter(" ")
        val userMail = hydraService.introspectToken(token).sub
        return if (userMail != null) {
            val dto  = userClientService.createUserClient(userMail)
            HttpResponse.ok(dto)
        } else {
            HttpResponse.unauthorized()
        }
    }

    @Get(value = "/client/{clientId}")
    fun getUserFromClient(
            @PathVariable clientId: String
    ): HttpResponse<String> {
        throw APIException(APIExceptionCode.UNKNOWN_USER)
        val userId = userClientService.userIdForClient(clientId)
        return if (userId != null) {
            HttpResponse.ok(userId)
        } else {
            HttpResponse.notFound()
        }
    }

}