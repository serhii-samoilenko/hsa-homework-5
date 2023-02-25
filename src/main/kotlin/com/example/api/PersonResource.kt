package com.example.api

import com.example.service.PersonService
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.MediaType.TEXT_PLAIN
import javax.ws.rs.core.Response

@ApplicationScoped
@Path("/person")
class PersonResource(
    private val personService: PersonService
) {

    @GET
    @Produces(APPLICATION_JSON)
    fun getPerson(
        @QueryParam("firstName") firstName: String,
        @QueryParam("lastName") lastName: String
    ): Response {
        val entity = personService.getPerson(firstName, lastName)
        return if (entity == null) {
            Response.noContent().build()
        } else {
            Response.ok(entity).build()
        }
    }

    @POST
    @Produces(APPLICATION_JSON)
    fun createPerson(
        person: Person
    ): Response {
        return Response.ok(personService.persistPerson(person)).build()
    }

    @GET
    @Path("/random")
    @Produces(APPLICATION_JSON)
    fun getRandomPerson(): Response {
        return Response.ok(personService.getRandomPerson()).build()
    }

    @POST
    @Path("/random")
    @Produces(APPLICATION_JSON)
    fun createRandomPerson(): Response {
        return Response.ok(personService.createRandomPerson()).build()
    }

    @DELETE
    @Path("/all")
    fun deleteAllPersons(): Response {
        personService.deleteAllPersons()
        return Response.noContent().build()
    }

    @GET
    @Path("/count")
    @Produces(TEXT_PLAIN)
    fun countPersons(): Response {
        return Response.ok(personService.countPersons()).build()
    }
}
