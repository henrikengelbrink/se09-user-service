package se09.user.service.ws

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.dto.CertResponseDTO
import se09.user.service.dto.CreateCertificateDTO
import se09.user.service.models.ClientType
import java.net.URL
import javax.inject.Singleton

@Singleton
class CertService {

    @Value("\${service.url.cert}")
    private lateinit var certServiceUrl: String

    private val LOG: Logger = LoggerFactory.getLogger(CertService::class.java)

    fun createCert(clientId: String): CertResponseDTO {
        LOG.info("createCert")
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
