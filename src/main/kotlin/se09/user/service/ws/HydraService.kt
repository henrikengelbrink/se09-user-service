package se09.user.service.ws

import com.beust.klaxon.Klaxon
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpRequest.PUT
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.reactivex.Flowable
import se09.user.service.dto.*
import java.awt.print.Book
import java.net.URL
import java.util.*
import javax.inject.Singleton


@Singleton
class HydraService {

    //@Value("\${hydra.url.public}")
    private val hydraPublicUrl: String? = "http://localhost:4445"

    fun getLoginRequest(challenge: String): HydraLoginRequestDTO {
        println("getLoginRequest")
        val httpClient = RxHttpClient.create(URL(hydraPublicUrl))
        val endpoint = getHydraRequestEndpoint(challenge, HydraRequestType.LOGIN)
        val response = httpClient.toBlocking().retrieve(endpoint)
        val jsonResponse = Klaxon().parse<HydraLoginRequestDTO>(response)
        return jsonResponse!!
    }

    fun acceptLoginRequest(dto: HydraLoginRequestDTO): HydraRedirectDTO {
        println("acceptLoginRequest HydraLoginRequestDTO")
        val httpClient = RxHttpClient.create(URL(hydraPublicUrl))
        val endpoint = getHydraAcceptRequestEndpoint(dto.challenge, HydraRequestType.LOGIN)

        val response = httpClient.toBlocking().exchange(
                PUT(endpoint, dto.alwaysRememberAcceptPayload).contentType(MediaType.APPLICATION_JSON_TYPE),
                HydraRedirectDTO::class.java
        )
        return response.body()!!
    }

    fun acceptLoginRequest(dto: LoginPayloadDTO): HydraRedirectDTO {
        println("acceptLoginRequest LoginPayloadDTO")
        val httpClient = RxHttpClient.create(URL(hydraPublicUrl))
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
        println("handleConsent")
        val consentRequestDTO = getConsentRequest(challenge)
        return acceptConsentRequest(consentRequestDTO)
    }

    private fun getConsentRequest(challenge: String): HydraConsentRequestDTO {
        println("getConsentRequest")
        val httpClient = RxHttpClient.create(URL(hydraPublicUrl))
        val endpoint = getHydraRequestEndpoint(challenge, HydraRequestType.CONSENT)
        val response = httpClient.toBlocking().retrieve(endpoint)
        val jsonResponse = Klaxon().parse<HydraConsentRequestDTO>(response)
        return jsonResponse!!
    }

    private fun acceptConsentRequest(dto: HydraConsentRequestDTO): HydraRedirectDTO {
        println("acceptConsentRequest")
        val httpClient = RxHttpClient.create(URL(hydraPublicUrl))
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
