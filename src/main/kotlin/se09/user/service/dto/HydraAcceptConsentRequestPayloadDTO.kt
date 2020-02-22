package se09.user.service.dto

data class HydraAcceptConsentRequestPayloadDTO(
        val remember: Boolean,
        val remember_for: Int,
        val grant_scope: List<String>
)
