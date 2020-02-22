package se09.user.service.models

import javax.persistence.*

@Entity
@Table(name = "users")
class User(
        val email: String = "",
        @Column(name = "hashed_password")
        var hashedPassword: String = "",
        var salt: String = ""
): BaseEntity()
