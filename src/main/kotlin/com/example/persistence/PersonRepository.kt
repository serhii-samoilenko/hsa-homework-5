package com.example.persistence

import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonRepository : PanacheMongoRepository<PersonEntity> {

    fun findByFirstAndLastName(firstName: String, lastName: String) =
        find("firstName = ?1 and lastName = ?2", firstName, lastName).firstResult()
}
