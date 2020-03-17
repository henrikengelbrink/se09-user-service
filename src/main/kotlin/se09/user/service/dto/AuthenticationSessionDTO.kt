package se09.user.service.dto

data class AuthenticationSessionDTO(
    val subject: String,
    val extra: MutableMap<String, Any>,
    val header: MutableMap<String, Any>
)
