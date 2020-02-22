package se09.user.service.controller

import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import se09.user.service.dto.AuthType
import se09.user.service.dto.LoginPayloadDTO
import se09.user.service.exceptions.APIException
import se09.user.service.services.UserService
import se09.user.service.ws.HydraService
import java.io.File
import java.lang.Exception
import java.net.URI
import java.net.URLEncoder
import javax.inject.Inject


@Controller("/auth")
class AuthController {

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var hydraService: HydraService

    @Post(value = "/register", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun register(email: String,  password: String, challenge: String): HttpResponse<Any> {
        println("register")
        val dto = LoginPayloadDTO(
                email = email,
                password = password,
                challenge = challenge
        )
        return authenticate(dto, AuthType.REGISTER)
    }

    @Post(value = "/login", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun login(email: String,  password: String, challenge: String): HttpResponse<Any> {
        println("login")
        val dto = LoginPayloadDTO(
                email = email,
                password = password,
                challenge = challenge
        )
        return authenticate(dto, AuthType.LOGIN)
    }

    @Get(value = "/register")
    fun getRegister(@QueryValue login_challenge: String, @QueryValue error: String?): HttpResponse<Any> {
        println("getRegister -> $error")
        return renderAuth(login_challenge, AuthType.REGISTER, error)
    }

    @Get(value = "/login")
    fun getLogin(@QueryValue login_challenge: String, @QueryValue error: String?): HttpResponse<Any> {
        println("getLogin -> $error")
        return renderAuth(login_challenge, AuthType.LOGIN, error)
    }

    @Get(value = "/consent")
    fun getConsent(@QueryValue consent_challenge: String): HttpResponse<Any> {
        println("getConsent")
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
            return HttpResponse.redirect(URI("http://localhost:8181/auth/${authType.value}?login_challenge=${loginPayload.challenge}&error=$errMsg"))
        }

        val hydraResponse = hydraService.acceptLoginRequest(loginPayload)
        return HttpResponse.redirect(URI(hydraResponse.redirect_to))
    }

    private fun renderAuth(challenge: String, authType: AuthType, errorMessage:String?): HttpResponse<Any> {
        val loginRequest = hydraService.getLoginRequest(challenge)
        if (loginRequest.skip) {
            val redirectDTO = hydraService.acceptLoginRequest(loginRequest)
            return HttpResponse.redirect(URI.create(redirectDTO.redirect_to))
        } else {
            val loader: ClassPathResourceLoader = ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
            val resource = loader.getResource("classpath:views/${authType.value}.html")
            if (resource.isPresent) {
                val file = File(resource.get().toURI())
                var content = file.readText(Charsets.UTF_8)
                content = content.replace("###CHALLENGE###",challenge)
                content = content.replace("###ERROR_MESSAGE###", errorMessage ?: "")
                val response = HttpResponse.ok(content)
                response.headers.add("Content-Type", "text/html")
                return response as HttpResponse<Any>
            } else {
                throw Exception()
            }

        }
    }

}