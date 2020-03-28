package se09.user.service.dto

data class MQTTRegisterDTO(
        val username: String,
        val clientId: String,
        val password: String
)
