package se09.user.service.exceptions

import io.micronaut.http.HttpStatus

enum class APIExceptionCode(val httpCode: HttpStatus) {
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    LOGIN_INVALID(HttpStatus.UNAUTHORIZED),
    UNKNOWN_USER(HttpStatus.NOT_FOUND),
    PASSWORD_PAWNED(HttpStatus.CONFLICT),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST),
}
