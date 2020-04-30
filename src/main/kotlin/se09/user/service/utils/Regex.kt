package se09.user.service.utils

object Regex {

    val PASSWORD_REGEX = "^[a-zA-Z@#\$%^&+=](?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=]).{16,}[a-zA-Z0-9]\$"

}