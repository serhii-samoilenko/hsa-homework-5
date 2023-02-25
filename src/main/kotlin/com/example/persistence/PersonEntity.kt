package com.example.persistence

import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.types.ObjectId

@MongoEntity(collection = "Person")
class PersonEntity {
    var id: ObjectId? = null
    lateinit var firstName: String
    lateinit var lastName: String
    var age: Int = 0
    lateinit var phoneNumber: String
}
