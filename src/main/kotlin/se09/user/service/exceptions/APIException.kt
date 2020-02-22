package se09.user.service.exceptions

class APIException(val code: APIExceptionCode) : RuntimeException()