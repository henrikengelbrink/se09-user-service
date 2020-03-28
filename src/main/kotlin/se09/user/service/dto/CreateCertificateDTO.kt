package se09.user.service.dto

import se09.user.service.models.ClientType

data class CreateCertificateDTO(
    val clientId: String,
    val clientType: ClientType
)
