package se09.user.service.models

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_clients")
class UserClient(
        @Column(name = "user_id")
        var userId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
): BaseEntity()
