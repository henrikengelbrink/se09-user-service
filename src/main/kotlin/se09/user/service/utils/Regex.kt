package se09.user.service.utils

object Regex {

    val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#\$^+=!*()@%&]).{8,10}\$"

}