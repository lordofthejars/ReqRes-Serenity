package com.lordofthejars.reqres;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.util.EnvironmentVariables;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class WhenManagingUsers {

    private String theRestApiBaseUrl;
    private EnvironmentVariables environmentVariables;
    private Actor sam;


    @Before
    public void configureBaseUrl() {
        theRestApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                                                   .orElseThrow(IllegalArgumentException::new);

        sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at(theRestApiBaseUrl));
    }

    @Test
    public void list_all_users() {
        sam.attemptsTo(
            listAllUsers()
        );

        sam.should(
                seeThatResponse("all the expected users should be returned",
                        response -> response.statusCode(200)
                                            .body("data.first_name", hasItems("George", "Janet", "Emma")))
        );
    }

    @Test
    public void find_an_individual_user() {
        sam.attemptsTo(
            FindAUser.withId(1)
        );

        sam.should(
                seeThatResponse( "User details should be correct",
                        response -> response.statusCode(200)
                                            .body("data.first_name", equalTo("George"))
                                            .body("data.last_name", equalTo("Bluth"))
                )
        );
    }

    public Task listAllUsers() {
        return Task.where("{0} lists all users",
                Get.resource("/users")
        );
    }

}