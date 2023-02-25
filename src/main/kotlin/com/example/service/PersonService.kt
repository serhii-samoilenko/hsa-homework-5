package com.example.service

import com.example.api.Person
import com.example.api.toEntity
import com.example.api.toPerson
import com.example.config.AppConfig
import com.example.persistence.PersonRepository
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import org.apache.commons.lang3.RandomStringUtils
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import kotlin.math.sqrt
import kotlin.random.Random

@Startup
@ApplicationScoped
class PersonService(
    private val personRepository: PersonRepository,
    private val config: AppConfig
) {
    private val firstNamesCount = sqrt(config.entityCount().toDouble()).toInt()
    private val lastNamesCount = firstNamesCount
    private val firstNames: List<String>
    private val lastNames: List<String>

    init {
        Log.info("Creating pool of $firstNamesCount first names and $lastNamesCount last names")
        firstNames = (1..firstNamesCount).map { randomName(19) }.toList()
        lastNames = (1..lastNamesCount).map { randomName(20) }.toList()
    }

    fun getPerson(firstName: String, lastName: String): Person? =
        personRepository.findByFirstAndLastName(firstName, lastName)?.toPerson()

    fun persistPerson(person: Person): Person {
        val found = personRepository.findByFirstAndLastName(person.firstName, person.lastName)
        return if (found != null) {
            found.age = person.age
            found.phoneNumber = person.phoneNumber
            personRepository.update(found)
            found.toPerson()
        } else {
            personRepository.persist(person.toEntity())
            person
        }
    }

    fun getRandomPerson() = getPerson(firstNames.random(), lastNames.random())

    fun createRandomPerson() = Person(
        firstName = firstNames.random(),
        lastName = lastNames.random(),
        age = randomAge(),
        phoneNumber = randomPhoneNumber()
    ).also { persistPerson(it) }

    fun deleteAllPersons() = personRepository.deleteAll()

    fun countPersons() = personRepository.count()

    @PostConstruct
    fun init() {
        Log.info(
            "Pre-populating DB with ${config.prepopulatePercentage()}% of total" +
                " ${firstNames.size} first names and ${lastNames.size} last names"
        )
        firstNames.forEach { firstName ->
            val count: Int = (lastNamesCount * config.prepopulatePercentage() / 100)
            val personList = (1..count).map {
                Person(
                    firstName = firstName,
                    lastName = lastNames[it],
                    age = randomAge(),
                    phoneNumber = randomPhoneNumber()
                )
            }.map { it.toEntity() }.toList()
            Log.info("Persisting ${personList.size} persons with first name $firstName")
            personRepository.persist(personList)
        }
        Log.info("Persisted ${countPersons()} persons in total")
    }

    companion object {
        private fun randomName(length: Int) = RandomStringUtils.randomAlphabetic(length)
        private fun randomAge() = Random.nextInt(0, 100)
        private fun randomPhoneNumber() = RandomStringUtils.randomNumeric(10)
    }
}
