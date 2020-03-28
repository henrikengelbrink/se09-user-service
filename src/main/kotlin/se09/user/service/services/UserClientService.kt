package se09.user.service.services

import se09.user.service.dto.UserClientResponseDTO
import se09.user.service.models.UserClient
import se09.user.service.repositories.UserClientRepository
import se09.user.service.ws.CertService
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserClientService {

    @Inject
    private lateinit var certService: CertService

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var userClientRepository: UserClientRepository

    fun createUserClient(email: String): UserClientResponseDTO {
        val userId = userService.userIdByEmail(email)
        if (userId != null) {
            var userClient = UserClient(
                    userId = UUID.fromString(userId)
            )
            userClient = userClientRepository.save(userClient)
            val certDTO = certService.createCert(userClient.id.toString())
            return UserClientResponseDTO(
                    clientId = userClient.id.toString(),
                    certificate = certDTO.certificate,
                    key = certDTO.key
            )
        } else {
            throw Exception()
        }
    }

}
