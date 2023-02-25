package com.example.api

import com.example.persistence.PersonEntity

data class Person(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val phoneNumber: String
)

fun PersonEntity.toPerson() = Person(
    id = id!!.toString(),
    firstName = firstName,
    lastName = lastName,
    age = age,
    phoneNumber = phoneNumber
)

fun Person.toEntity() = PersonEntity().also {
    it.firstName = firstName
    it.lastName = lastName
    it.age = age
    it.phoneNumber = phoneNumber
}
