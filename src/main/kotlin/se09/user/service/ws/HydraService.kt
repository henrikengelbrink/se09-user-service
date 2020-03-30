package se09.user.service.ws

import com.beust.klaxon.Klaxon
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpRequest.PUT
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import se09.user.service.dto.*
import java.net.URL
import javax.inject.Singleton

@Singleton
class HydraService {

    @Value("\${hydra.url.admin}")
    private lateinit var hydraAdminUrl: String

    fun getLoginRequest(challenge: String): HydraLoginRequestDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))
        val endpoint = getHydraRequestEndpoint(challenge, HydraRequestType.LOGIN)
        val response = httpClient.toBlocking().retrieve(endpoint)
        val jsonResponse = Klaxon().parse<HydraLoginRequestDTO>(response)
        return jsonResponse!!
    }

    fun acceptLoginRequest(dto: HydraLoginRequestDTO): HydraRedirectDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))
        val endpoint = getHydraAcceptRequestEndpoint(dto.challenge, HydraRequestType.LOGIN)

        val response = httpClient.toBlocking().exchange(
                PUT(endpoint, dto.alwaysRememberAcceptPayload).contentType(MediaType.APPLICATION_JSON_TYPE),
                HydraRedirectDTO::class.java
        )
        return response.body()!!
    }

    fun acceptLoginRequest(dto: LoginPayloadDTO): HydraRedirectDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))
        val endpoint = getHydraAcceptRequestEndpoint(dto.challenge, HydraRequestType.LOGIN)

        val payload = HydraAcceptLoginRequestPayloadDTO(
                remember = true,
                remember_for = 0,
                subject = dto.email
        )
        val response = httpClient.toBlocking().exchange(
                PUT(endpoint, payload).contentType(MediaType.APPLICATION_JSON_TYPE),
                HydraRedirectDTO::class.java
        )
        return response.body()!!
    }

    fun handleConsent(challenge: String): HydraRedirectDTO {
        val consentRequestDTO = getConsentRequest(challenge)
        return acceptConsentRequest(consentRequestDTO)
    }

    fun introspectToken(token: String): HydraIntrospectDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))

        val payload = mapOf(
                "token" to token
        )
        val response = httpClient.toBlocking().exchange(
                POST("/oauth2/introspect", payload).contentType(MediaType.APPLICATION_FORM_URLENCODED),
                HydraIntrospectDTO::class.java
        )
        return response.body()!!
    }

    private fun getConsentRequest(challenge: String): HydraConsentRequestDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))
        val endpoint = getHydraRequestEndpoint(challenge, HydraRequestType.CONSENT)
        val response = httpClient.toBlocking().retrieve(endpoint)
        val jsonResponse = Klaxon().parse<HydraConsentRequestDTO>(response)
        return jsonResponse!!
    }

    private fun acceptConsentRequest(dto: HydraConsentRequestDTO): HydraRedirectDTO {
        val httpClient = RxHttpClient.create(URL(hydraAdminUrl))
        val endpoint = getHydraAcceptRequestEndpoint(dto.challenge, HydraRequestType.CONSENT)

        val response = httpClient.toBlocking().exchange(
                PUT(endpoint, dto.alwaysRememberAcceptPayload).contentType(MediaType.APPLICATION_JSON_TYPE),
                HydraRedirectDTO::class.java
        )
        return response.body()!!
    }

    private fun getHydraRequestEndpoint(challenge: String, type: HydraRequestType): String {
        return "/oauth2/auth/requests/${type.value}?${type.challengeKey()}=$challenge"
    }

    private fun getHydraAcceptRequestEndpoint(challenge: String, type: HydraRequestType): String {
        return "/oauth2/auth/requests/${type.value}/accept?${type.challengeKey()}=$challenge"
    }


}
