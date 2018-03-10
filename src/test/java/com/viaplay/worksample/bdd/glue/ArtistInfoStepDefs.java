package com.viaplay.worksample.bdd.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.bdd.WorkSampleSystemTest;
import com.viaplay.worksample.domain.dto.ArtistInfoDto;
import com.viaplay.worksample.exception.handler.ErrorResponseBody;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.JsonAssert.*;
import static net.javacrumbs.jsonunit.core.Option.*;

public class ArtistInfoStepDefs extends WorkSampleSystemTest {

    private ResponseEntity response;
    private ObjectMapper mapper = new ObjectMapper();

    @When("^the client calls api GET \"([^\"]*)\" with \"([^\"]*)\"$")
    public void the_client_calls_api_GET_with(String artistInfoAPI, String artistMbid) {
        response = sendHttpGetRequest(artistInfoAPI + artistMbid, ArtistInfoDto.class);
    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) {
        HttpStatus actualStatusCode = response.getStatusCode();
        assertThat(actualStatusCode.value()).isEqualTo(statusCode);
    }

    @And("^the response body is identical to the expected json \"([^\"]*)\"$")
    public void theResponseBodyIsIdenticalToTheExpectedJson(String file) throws Throwable {
        ArtistInfoDto expected = loadJsonStringFromFile(file, ArtistInfoDto.class);

        assertJsonEquals(response.getBody(), expected, when(IGNORING_ARRAY_ORDER));
    }

    @When("^the client calls api GET \"([^\"]*)\" with \"([^\"]*)\" that does not exist$")
    public void theClientCallsApiGETWithThatDoesNotExist(String artistInfoAPI, String artistMbid) {
        response = sendHttpGetRequest(artistInfoAPI + artistMbid, ErrorResponseBody.class);
    }

    @And("^the error response body is identical to the expected json \"([^\"]*)\"$")
    public void theErrorResponseBodyIsIdenticalToTheExpectedJson(String file) throws Throwable {
        ErrorResponseBody expected = loadJsonStringFromFile(file, ErrorResponseBody.class);
        assertThat(mapper.writeValueAsString(response.getBody())).isEqualTo(mapper.writeValueAsString(expected));
    }

    private ResponseEntity sendHttpGetRequest(String url, Class responseBodyType) {
        return restTemplate.getForEntity(url, responseBodyType);
    }

    private <T> T loadJsonStringFromFile(String file, Class<T> typeClass) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return mapper.readValue(new File(classLoader.getResource(file).getFile()), typeClass);
    }
}
