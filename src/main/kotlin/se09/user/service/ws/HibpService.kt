package se09.user.service.ws

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import javax.inject.Singleton

@Singleton
class HibpService {

    @Value("\${service.url.hibp}")
    private lateinit var hibpServiceUrl: String

    fun passwordValid(password: String): Boolean {
        val httpClient = RxHttpClient.create(URL(hibpServiceUrl))
        val hashedPassword = plainPasswordToHash(password)
        val payload = mapOf(
                "hash" to hashedPassword
        )
        val response = httpClient.toBlocking().exchange(
                POST("/certificates", payload).contentType(MediaType.APPLICATION_JSON),
                Map::class.java
        )
        return response.status == HttpStatus.OK
    }

    private fun plainPasswordToHash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.reset()
        digest.update(password.toByteArray())
        return String.format("%040x", BigInteger(1, digest.digest()))
    }
}
