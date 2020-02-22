package se09.user.service.dto

enum class HydraRequestType(val value: String) {
    LOGIN("login"),
    CONSENT("consent");

    fun challengeKey(): String {
        return when (this) {
            LOGIN -> "login_challenge"
            CONSENT -> "consent_challenge"
        }
    }

}