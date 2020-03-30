package se09.user.service.ws

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import se09.user.service.dto.CertResponseDTO
import se09.user.service.dto.CreateCertificateDTO
import se09.user.service.models.ClientType
import java.net.URL
import javax.inject.Singleton

@Singleton
class CertService {

    @Value("\${service.url.cert}")
    private lateinit var certServiceUrl: String

    fun createCert(clientId: String): CertResponseDTO {
        val httpClient = RxHttpClient.create(URL(certServiceUrl))
        val payload = CreateCertificateDTO(
                clientId = clientId,
                clientType = ClientType.USER
        )
        val response = httpClient.toBlocking().exchange(
                POST("/certificates", payload).contentType(MediaType.APPLICATION_JSON),
                CertResponseDTO::class.java
        )
        return response.body()!!
    }

}
