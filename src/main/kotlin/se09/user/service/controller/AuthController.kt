package se09.user.service.controller

import io.micronaut.context.annotation.Value
import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.dto.AuthType
import se09.user.service.dto.LoginPayloadDTO
import se09.user.service.exceptions.APIException
import se09.user.service.services.UserService
import se09.user.service.ws.HydraService
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*
import javax.inject.Inject


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
    fun register(email: String,  password: String, challenge: String): HttpResponse<Any> {
        LOG.warn("register")
        val dto = LoginPayloadDTO(
                email = email,
                password = password,
                challenge = challenge
        )
        return authenticate(dto, AuthType.REGISTER)
    }

    @Post(value = "/login", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun login(email: String,  password: String, challenge: String): HttpResponse<Any> {
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

    private fun authenticate(loginPayload: LoginPayloadDTO, authType: AuthType): HttpResponse<Any> {
        try {
            when(authType) {
                AuthType.LOGIN -> userService.loginUser(loginPayload)
                AuthType.REGISTER -> userService.registerUser(loginPayload)
            }
        } catch (e: APIException) {
            var errMsg = "unknownUser"
            if (authType == AuthType.REGISTER) {
                errMsg = "userExists"
            }
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
            //val loader = ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
            //val resource: Optional<URL> = loader.getResource("classpath:views/${authType.value}.html")

//            val resource = ResourceResolver().getResource("classpath:views/${authType.value}.html")
//            println(resource.isPresent)
//            println(resource.get().toString())
//            if (resource.isPresent) {
                val uri = URI.create("/${authType.value}.html")
                println(uri.toString())
                val file = File(uri)
                var content = file.readText(Charsets.UTF_8)
                content = content.replace("###CHALLENGE###", challenge)

                val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                val randomCSRFToken = (1..10)
                        .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                        .map(charPool::get)
                        .joinToString("");
                LOG.warn("******* CSRF $randomCSRFToken")

                content = content.replace("###CSRF_TOKEN###",randomCSRFToken)

                content = content.replace("###ERROR_MESSAGE###", errorMessage ?: "")
                response = HttpResponse.ok(content)
                response.headers.add("Content-Type", "text/html")
                response.headers.add("Set-Cookie", "oauth2_authentication_csrf=$randomCSRFToken")
//            } else {
//                throw Exception()
//            }
        }
        return response
    }

}