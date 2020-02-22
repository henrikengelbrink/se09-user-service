package se09.user.service.dto

data class HydraConsentRequestDTO(
        val challenge: String,
        val skip: Boolean,
        val requested_scope: List<String>
) {

    val alwaysRememberAcceptPayload: HydraAcceptConsentRequestPayloadDTO = HydraAcceptConsentRequestPayloadDTO(
            remember = !skip,
            remember_for = 0,
            grant_scope = requested_scope
    )

}
