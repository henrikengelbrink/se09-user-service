package se09.user.service.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import se09.user.service.models.User
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    fun findByEmail(email: String): User?

}
