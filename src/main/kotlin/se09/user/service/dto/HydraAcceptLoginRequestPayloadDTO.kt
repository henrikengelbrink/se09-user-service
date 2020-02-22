package se09.user.service.dto

data class HydraAcceptLoginRequestPayloadDTO(
        val remember: Boolean,
        val remember_for: Int,
        val subject: String?
)
