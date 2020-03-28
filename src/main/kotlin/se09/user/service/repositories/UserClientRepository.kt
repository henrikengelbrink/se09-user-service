package se09.user.service.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import se09.user.service.models.UserClient
import java.util.*

@Repository
interface UserClientRepository : CrudRepository<UserClient, UUID>
