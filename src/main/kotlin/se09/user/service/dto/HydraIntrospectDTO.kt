package se09.user.service.dto

data class HydraIntrospectDTO(
    val active: Boolean,
    val scope: String,
    val client_id: String,
    val sub: String,
    val exp: Float,
    val iat: Float,
    val iss: String,
    val token_type: String
)
