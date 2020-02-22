package se09.user.service.dto

data class HydraLoginRequestDTO(
        val challenge: String,
        val skip: Boolean,
        val subject: String?
) {

    val alwaysRememberAcceptPayload: HydraAcceptLoginRequestPayloadDTO = HydraAcceptLoginRequestPayloadDTO(
            remember = !skip,
            remember_for = 0,
            subject = subject
    )

}
