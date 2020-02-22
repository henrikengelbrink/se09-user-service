package se09.user.service.dto

import se09.user.service.exceptions.APIException
import se09.user.service.exceptions.APIExceptionCode
import se09.user.service.utils.Regex

data class LoginPayloadDTO(
        val email: String,
        val password: String,
        val challenge: String
) {


    fun validate() {
//        if(!Regex.PASSWORD_REGEX.toRegex().matches(password)) {
//            throw APIException(APIExceptionCode.INVALID_PASSWORD_FORMAT)
//        }
    }

}
