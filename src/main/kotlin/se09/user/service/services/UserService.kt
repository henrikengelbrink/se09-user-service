package se09.user.service.services

import at.favre.lib.crypto.bcrypt.BCrypt
import se09.user.service.dto.LoginPayloadDTO
import se09.user.service.dto.UserResponseDTO
import se09.user.service.exceptions.APIException
import se09.user.service.exceptions.APIExceptionCode
import se09.user.service.models.User
import se09.user.service.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService {

    @Inject
    private lateinit var userRepository: UserRepository

    fun registerUser(loginPayload: LoginPayloadDTO): UserResponseDTO {
        loginPayload.validate()
        var user = userRepository.findByEmail(loginPayload.email)
        if (user != null) {
            throw APIException(APIExceptionCode.USER_ALREADY_EXISTS)
        }
        val hashedPassword: String = BCrypt.withDefaults().hashToString(12, loginPayload.password.toCharArray())

        user = User(
                email = loginPayload.email,
                hashedPassword = hashedPassword
        )
        user = userRepository.save(user)
        return UserResponseDTO(
                id = user.id.toString(),
                email = user.email
        )
    }

    fun loginUser(loginPayload: LoginPayloadDTO): UserResponseDTO {
        loginPayload.validate()

        val user = userRepository.findByEmail(loginPayload.email) ?: throw APIException(APIExceptionCode.UNKNOWN_USER)

        val result: BCrypt.Result = BCrypt.verifyer().verify(loginPayload.password.toCharArray(), user.hashedPassword)
        if (!result.verified) {
            throw APIException(APIExceptionCode.LOGIN_INVALID)
        }
        return UserResponseDTO(
                id = user.id.toString(),
                email = user.email
        )
    }

    fun userIdByEmail(email: String): String? {
        var userId: String? = null
        try {
            val user = userRepository.findByEmail(email) ?: throw APIException(APIExceptionCode.UNKNOWN_USER)
            userId = user.id.toString()
        } catch (e: Exception) {

        }
        return userId
    }


}
