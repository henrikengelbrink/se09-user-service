package se09.user.service.dto

data class UserClientResponseDTO(
    val clientId: String,
    val certificate: String,
    val key: String
)
