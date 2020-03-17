package se09.user.service.controller

import io.micronaut.context.annotation.Value
import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.dto.AuthType
import se09.user.service.dto.AuthenticationSessionDTO
import se09.user.service.dto.LoginPayloadDTO
import se09.user.service.exceptions.APIException
import se09.user.service.services.UserService
import se09.user.service.ws.HydraService
import java.net.URI
import javax.inject.Inject
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Validated
@Controller("/auth")
class AuthController {

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var hydraService: HydraService

    @Value("\${micronaut.application.externalhost}")
    private lateinit var externalHostname: String

    private val LOG: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @Post(value = "/register", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun register(
            @NotBlank @Email email: String,
            @Size(min = 10, max = 64, message = "Password should have at least 10 chars") @NotBlank password: String,
            @NotBlank challenge: String
    ): HttpResponse<Any> {
        LOG.warn("register")
        val dto = LoginPayloadDTO(
                email = email,
                password = password,
                challenge = challenge
        )
        return authenticate(dto, AuthType.REGISTER)
    }

    @Post(value = "/login", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun login(
            @NotBlank @Email email: String,
            @Size(min = 10, max = 64, message = "Password should have at least 10 chars") @NotBlank password: String,
            @NotBlank challenge: String
    ): HttpResponse<Any> {
        LOG.warn("login")
        val dto = LoginPayloadDTO(
                email = email,
                password = password,
                challenge = challenge
        )
        return authenticate(dto, AuthType.LOGIN)
    }

    @Get(value = "/register")
    fun getRegister(
            @QueryValue login_challenge: String,
            @QueryValue error: String?
            //@CookieValue("oauth2_authentication_csrf") csrfCookie: String
    ): HttpResponse<Any> {
        LOG.warn("getRegister -> $error")
        return renderAuth(login_challenge, AuthType.REGISTER, error, "csrfCookie")
    }

    @Get(value = "/login")
    fun getLogin(
            @QueryValue login_challenge: String,
            @QueryValue error: String?
            //@CookieValue("oauth2_authentication_csrf") csrfCookie: String
    ): HttpResponse<Any> {
        LOG.warn("getLogin -> $error # ")
        return renderAuth(login_challenge, AuthType.LOGIN, error, "csrfCookie")
    }

    @Get(value = "/consent")
    fun getConsent(
            @QueryValue consent_challenge: String
    ): HttpResponse<Any> {
        LOG.warn("getConsent")
        val redirect = hydraService.handleConsent(challenge = consent_challenge)
        return HttpResponse.redirect(URI(redirect.redirect_to))
    }

    @Post(value = "/hydrator")
    fun postHydrator(
        @Body dto: AuthenticationSessionDTO
    ): HttpResponse<Any> {
        LOG.warn("hydrator ${dto.subject}")
        val introspectResult = hydraService.introspectToken(dto.subject)
        var userId: String? = null
        LOG.warn("hydrator introspect ${introspectResult.active}")
        LOG.warn("hydrator introspect ${introspectResult.sub}")
        if (introspectResult.active && introspectResult.sub != null) {
            userId = userService.userIdByEmail(introspectResult.sub!!)
        }
        LOG.warn("userId $userId")
        if (userId != null) {
            dto.header["X-User-Id"] = userId
        } else {
            LOG.warn("hydrator - user not found ${dto.subject}")
        }
        LOG.warn(dto.header.get("X-User-Id") as String)
        return HttpResponse.ok(dto)
    }

    private fun authenticate(loginPayload: LoginPayloadDTO, authType: AuthType): HttpResponse<Any> {
        try {
            when(authType) {
                AuthType.LOGIN -> userService.loginUser(loginPayload)
                AuthType.REGISTER -> userService.registerUser(loginPayload)
            }
        } catch (e: APIException) {
//            var errMsg = "unknownUser"
//            if (authType == AuthType.REGISTER) {
//                errMsg = "userExists"
//            }
            val errMsg = "UnknownError"
            return HttpResponse.redirect(URI("${externalHostname}/auth/${authType.value}?login_challenge=${loginPayload.challenge}&error=$errMsg"))
        }
        val hydraResponse = hydraService.acceptLoginRequest(loginPayload)
        return HttpResponse.redirect(URI(hydraResponse.redirect_to))
    }

    private fun renderAuth(challenge: String, authType: AuthType, errorMessage:String?, csrfCookie: String): HttpResponse<Any> {
        LOG.warn("####### INPUT CSRF: $csrfCookie")
        val loginRequest = hydraService.getLoginRequest(challenge)
        val response: HttpResponse<Any>
        if (loginRequest.skip) {
            val redirectDTO = hydraService.acceptLoginRequest(loginRequest)
            response = HttpResponse.redirect(URI.create(redirectDTO.redirect_to))
        } else {

            var content = javaClass.getResource("/views/${authType.value}.html").readText()
            content = content.replace("###CHALLENGE###", challenge)

//            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//            val randomCSRFToken = (1..10)
//                    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
//                    .map(charPool::get)
//                    .joinToString("")
//            LOG.warn("******* CSRF $randomCSRFToken")

//            content = content.replace("###CSRF_TOKEN###", randomCSRFToken)
            content = content.replace("###EXTERNAL_HOSTNAME###", externalHostname)

            content = content.replace("###ERROR_MESSAGE###", errorMessage ?: "")
            response = HttpResponse.ok(content)
            response.headers.add("Content-Type", "text/html")
//            response.headers.add("Set-Cookie", "oauth2_authentication_csrf=$randomCSRFToken")
        }
        return response
    }

}