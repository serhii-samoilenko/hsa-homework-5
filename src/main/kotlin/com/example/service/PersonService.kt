package com.example.service

import com.example.config.AppConfig
import com.example.model.Person
import com.example.model.toEntity
import com.example.model.toPerson
import com.example.persistence.PersonRepository
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import org.apache.commons.lang3.RandomStringUtils
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import kotlin.math.sqrt

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

    fun createRandomPerson() = persistPerson(Person.generateRandom())

    fun deleteAllPersons() = personRepository.deleteAll()

    fun countPersons() = personRepository.count()

    @PostConstruct
    fun init() {
        Log.info("Deleting all existing records from DB")
        deleteAllPersons()
        Log.info(
            "Pre-populating DB with around ${config.prepopulatePercentage()}% of total" +
                " ${firstNames.size} first names and ${lastNames.size} last names"
        )
        val batchSize = 1000
        // Save random persons to mongo in batches:
        val count: Int = (firstNamesCount * lastNamesCount * config.prepopulatePercentage() / 100)
        (1..count)
            .asSequence()
            .map { Person.generateRandom(firstName = firstNames.random(), lastName = lastNames.random()) }
            .map { it.toEntity() }
            .chunked(batchSize)
            .forEach { personList ->
                Log.info("Persisting ${personList.size} persons")
                personRepository.persist(personList)
            }
        Log.info("DB now contains ${countPersons()} persons in total")
    }

    companion object {
        private fun randomName(length: Int) = RandomStringUtils.randomAlphabetic(length, length + 2)
    }
}
