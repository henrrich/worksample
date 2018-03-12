# Viaplay worksample Test
This project implements a REST API for providing clients with information about a specific music artist responding to an HTTP Get request.
``` java
http://<server ip>:8080/api/v1/artistinfo/{mbid}
```
Value of `{mbid}` represents the artist identifier in [MusicBrainz identifier](https://musicbrainz.org/doc/MusicBrainz_Identifier) format.

On success, the REST API will return HTTP status code `200` and the response body will contain the artist information including artist mbid, profile description and all released albums with links to their cover art images owned by the artist in the following JSON format:
```
{
  "mbid": "string", // artist mbid from the request
  "description": "string", // a text description fetched from Discogs.com
  "albums": [ // a list of albums for the artist
    {
      "id": "string", // album mbid
      "title": "string", // title of the album
      "images": [ // a list of http links to all the cover art images of the album, fetched from CoverArtArchive API
        "string"
      ]
    },
    ... // more albums
  ]
}
```

In order to compile the response of the artist information, the following 3rd party REST APIs are used:

* MusicBrainz Artist API to get information of albums and other related sources of the artist:
```
http://musicbrainz.org/ws/2/artist/{mbid}?&fmt=json&inc=url-rels+release-groups
```
* Cover Art Archive API to get all the http links to the cover art images of a specific album:
```
http://coverartarchive.org/release-group/{album mbid}
``` 
* Discogs API to get profile description text for the artist:
```
https://api.discogs.com/artists/{discogs artist id}
```

In case of error, the REST API will return the following HTTP status codes:

* 400 - Bad Request
* 404 - Artist Not Found
* 500 - Internal Server Error
* 503 - API Rate Limit Reached

The JSON reponse body will show more concrete error information, e.g.
```
{
    "timestamp": "2018-03-12T19:07:30.588+0100",
    "status": 400,
    "error": "Bad Request",
    "message": "MBID must follow UUID format! See https://en.wikipedia.org/wiki/Universally_unique_identifier\n",
    "path": "/api/v1/artistinfo/65f4f0c5-ef9e-490c-aee3"
}
```

### Project Setup
The REST service is implemented using:

* Spring Boot 2.0.0 release
* JDK 9
* Maven 3.3.9
* JUnit 4.12 for unit test
* Cucumber-jvm 1.2.5 for system test
* [json-unit](https://github.com/lukas-krecan/JsonUnit) for JSON comparison in system test
* [Swagger](https://swagger.io/) for REST API documentation

## Build and Run

### Build Application and Run Unit Tests
To build the application and run all unit tests, git clone the project from Github repo to a local folder and run maven command:
```shell
cd <project root folder>
mvn clean test
```

Around 10 unit tests are included in the project folder `<project root>/test/java/com/viaplay/worksample/unit`.
The unit tests are implemented to cover the controller and service layers of the application classes as well as some util classes.

### Run System Tests
[Cucumber](https://cucumber.io/) is used to implement the system tests in a BDD way. 
The test cases can be found under the folder `<project root>/test/java/com/viaplay/worksample/bdd` and its feature file can be found under `<project root>/test/resources/bdd`.

1 positive and 2 negative system test cases are provided.

For the positive case, the test will verify the HTTP response is `200` OK and the artist information fetched from response body contains the same data as in a predefined local JSON file.

For the negative cases, different `4xx` client error responses are verified.

To run the cucumber tests from command line, run `mvn test` command with `cucumber-test` profile:
```shell
cd <project root folder>
mvn clean test -Pcucumber-test
```
The cucumber html test reports can be found under `<project root>/target/reports/cucumber/html` folder and can be opened in a local browser.

With Spring MockMvc, the application will be booted up automatically on a random local port. And the system test can therefore run in a standalone mode without an pre-running application. 

**The positve system test case verification relies on predefined local json strings. Sometimes it may fail due to artist information has been updated in related data sources. In such case, local json string in the test resource file needs to be updated.**

### Package Application in Docker Container
A `Dockerfile` is also provided under the project root folder to support building the REST service application into a Java 9 docker image.
To build docker image, run:
```shell
cd <project root folder>
mvn clean package
```

And here is the command to run a container of the REST service (assume docker daemon is available):
```shell
docker run -p 8080:8080 -it <id of the image viaplay/worksample>
```

## API Design Thoughts
Here are some information about API design thoughts and implementation for this project.

### Response time: Multi-thread & Caching
In order to fetch the artist information, we need to make the following external REST API calls to different data sources:

* one call to MusicBrainz artist API to get list of albums and relations
* one call to Discogs API to get the profile description information
* for each album, make one call to CoverArtArchive API to fetch its image links

So for each incoming request, we need to make 1 + 1 + numberOfAlbums external REST API calls in total, 
and running all these calls in sequence will significant increase the response time due to network latency.

In the implementation, the Discogs API call and all the CoverArtArchive API calls will be executed in parallel separated threads.
In the `ArtistServiceImpl` and `CoverArtArchiveServiceImpl` service implementation, 
the following methods are marked with `@Async` annotation to inform Spring that the these two methods will be executed in a thread pool and their return values is intended to be cached.
```java
// ArtistServiceImpl.java
@Async
@Cacheable("profile")
public CompletableFuture<ArtistProfile> getProfileDescriptionForArtist(String id) { ... }

// CoverArtArchiveServiceImpl.java
@Async
@Cacheable("coverart")
public CompletableFuture<AlbumCoverArt> getAlbumCoverArt(String mbid) { ... }
```
The thread pool executor is declared in the project main class `WorksampleApplication.java`:
```java
@Bean
public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(threadPoolConfig.getSize());
    executor.setMaxPoolSize(threadPoolConfig.getSize());
    executor.setQueueCapacity(threadPoolConfig.getCapacity());
    executor.setThreadNamePrefix(threadPoolConfig.getPrefix());
    executor.initialize();
    return executor;
}
```
And in `application.properties` we can define the size and queue capacity of the thread pool:
```
# thread pool config
threadpool.size=20
threadpool.capacity=500
threadpool.prefix=worksample-
```

These external REST calls are running in parallel in `ArtistInfoController.java`:
```java
CompletableFuture<ArtistProfile> profileFuture = null;
if (artistIdInDiscogs != null) {
    profileFuture = artistService.getProfileDescriptionForArtist(artistIdInDiscogs); // issue discogs call and continue
}

Map<String, CompletableFuture<AlbumCoverArt>> futures = new HashMap<>();
Map<String, AlbumDto> albumsMap = new HashMap<>();
artist.getReleaseGroups().stream()
    .filter(releaseGroup -> "Album".equals(releaseGroup.getPrimaryType()))
    .forEach(album -> {
        String albumId = album.getId();
        AlbumDto albumDto = new AlbumDto(albumId, album.getTitle());
        albumsMap.put(albumId, albumDto);
        CompletableFuture<AlbumCoverArt> albumCoverArtFuture = coverArtArchiveService.getAlbumCoverArt(album.getId()); // issue coverartarchive call and continue
        futures.put(albumId, albumCoverArtFuture);
    });

CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[futures.size()])).join(); // wait for all coverartarchive api responses

ArtistProfile profile = profileFuture.get(); // wait for discogs api response
``` 

In addition to multi-threading, another way to improve the response time is to apply caching to the API response data,
so next time the same artist is queried, the cache will be hitted and cached results can be immediately returned to clients without actually performing those external REST API calls.

In this implementation, I just add the support for the Spring simple cache provider which uses concurrent maps in memory as the cache storage.
Methods that perform external REST API calls are marked with Spring `@Cacheable` annotation. When an artist is queried, only the first request will trigger external API calls.

**This simple cache solution has the drawback that cached data will never expire and can not be updated, the cache storage is in local memory and has the same life cycle as the application instance. It is only intended to show how cache can help improving response time here. Improvement here would be to integrate external cache storage e.g. Redis to the Spring Boot application.**

### Rate Limiting for Service Availability
One requirement of the REST service is to survive a high traffic load during peak time. 
And one challenge here is that the following external REST APIs have rate limiting:

* MusicBrainz API: allow 1 request per second per IP
* Discogs API: allow 60 requests per minute for authenticated client and 25 requests per minute for unauthenticated user

These API endpoints will return `503` Service Unavailable error when our traffic hit their limits, and even leads to further blocking of our client.
In addition to the cache approach mentioned previously, rate limiting feature can also be introduced here
to avoid exceeding the rate limits of our dependent services.

Rate limiting also protects the system befind the REST APIs against overloaded traffic. In case of peak traffic or DoS attack, overloaded client requests will be rejected in a polite manner to allow them regulate their traffic to a lower velocity. The benefit is to ensure the availability of the service even under high traffic that might bring the whole system down.

[Google guava rate limiter](https://google.github.io/guava/releases/19.0/api/docs/index.html?com/google/common/util/concurrent/RateLimiter.html) class implements a rate limiter that distributes permits at a configurable rate and is used here to implement a very simple rate limiter in this application.
The implementation is in class `RateLimitHandler.java`.
```java
public RateLimitHandler(RateLimitConfig rateLimitConfig) {
    this.rateLimiter = RateLimiter.create(rateLimitConfig.getRatelimitPerSec()); // set token distribution rate
}

// try to acquire permit within very short period, if succeed, continue. Otherwise throw RateLimitingException
public void checkPermit() {
    if (!rateLimiter.tryAcquire(rateLimitConfig.getCheckTimeout(), TimeUnit.MILLISECONDS)) {
        logger.warn("Rate limit reached!");
        throw new RateLimitingException("Reached rate limit " + getRateLimitPerSec() + " request per second!");
    }
}
```

And rate limiting check is performed at the beginning of request handling:
```java
@GetMapping("/artistinfo/{mbid}")
public ResponseEntity<ArtistInfoDto> getArtistInfoByMBID(@ValidMBID @PathVariable(value = "mbid", required = true) String mbid) throws Exception {
        logger.info("Fetching information of artist with MBID {}", mbid);

        rateLimitHandler.checkPermit(); // check rate limiting, if reached, throw RateLimitingException
        ...
}
```

In case the `RateLimitingException` is thrown, it will be caught by the global exception handler:
```java
@ExceptionHandler(value = { RateLimitingException.class })
protected ResponseEntity<Object> handleRateLimitingException(RuntimeException ex, WebRequest request) throws Exception {
    String message = ex.getMessage();
    String uri = UriUtil.getRequestUri(request);
    String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.SERVICE_UNAVAILABLE, message, uri);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("X-RateLimit-Limit", String.valueOf(rateLimitHandler.getRateLimitPerSec()));
    return handleExceptionInternal(ex, bodyOfResponse, 
        httpHeaders, HttpStatus.SERVICE_UNAVAILABLE, request);
}
```
Here we construct an error response with status code `503` Service Unavailable and set `X-RateLimit-Limit` http header with our rate limit value back to the client. So that they can get our limit value and try to regulate their traffic.

We can configure rate limit for the artist information API in `application.properties` file:
```
# rate limit control
ratelimit.rps=1 // rate limit: 1 request per second
ratelimit.checktimeout=10 // timeout for check if permit can be acquired, 10 millisec
```

### Error Handling
As mentioned earlier, the application will return the following error responses:
* 400 - Bad Request
* 404 - Artist Not Found
* 500 - Internal Server Error
* 503 - API Rate Limit Reached

All error reponse body will be compiled into the following JSON format, e.g.
```
{
    "timestamp": "2018-03-12T19:07:30.588+0100", // timestamp of the error
    "status": 400, // http response status code
    "error": "Bad Request", // http response status text
    "message": "MBID must follow UUID format! See https://en.wikipedia.org/wiki/Universally_unique_identifier\n", // detailed error message
    "path": "/api/v1/artistinfo/65f4f0c5-ef9e-490c-aee3" // uri of the client request
}
```

Class `RestExceptionHandler.java` registers global exception handlers for the application. Internal runtime exceptions will be caught by corresponding exception handler methods here and get transformed into proper error response body.

The REST service also deals with the error when accessing those external REST APIs. The `CoverArtRestErrorHandler`, `DiscogsRestErrorHandler` and `MusicBrainzRestHandler` handles exceptions triggered when trying to access those APIs
and convert them into customized internal exceptions that will be caught and handled properly in the service layer.

For example, when Discogs API is not accessible, the REST API will return the artist information with description field as `null`.
```java
ArtistProfile profile = profileFuture.get();
ArtistInfoDto artistInfoDto = new ArtistInfoDto(mbid, profile != null ? profile.getProfile() : null);
``` 

When CoverArtArchive API can not find the album image links, then it will appear as an empty array field of album images in `artistinfo` REST API response body for that specific album.

### Request Parameter Validation
The artist mbid sent in the request is going to be validated in order to reject invalid request earlier before sending it to any external REST services.

A customized JSR-303 validator is created to verify the mbid of the incoming requests.

The annotation `@ValidMBID` declares the constraint and its configurable properties. 
```java
@Constraint(validatedBy = MbidValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMBID {

    String message() default "MBID must follow UUID format! See https://en.wikipedia.org/wiki/Universally_unique_identifier";

    String pattern() default "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```
And the class `MbidValidator` implements the validation logic, where we match the mbid from the request against an UUID regex pattern.
```java
public class MbidValidator implements ConstraintValidator<ValidMBID, String> {

    private String pattern;

    @Override
    public void initialize(ValidMBID constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        Matcher m = Pattern.compile(pattern).matcher(s);
        return m.matches();
    }
}
```

And in `ArtistInfoController.java` we use the annotation to validate the mbid from the request:
```java
@GetMapping("/artistinfo/{mbid}")
public ResponseEntity<ArtistInfoDto> getArtistInfoByMBID(@ValidMBID @PathVariable(value = "mbid", required = true) String mbid) throws Exception { ... }
```

In case of invalid mbid, `ConstraintViolationException` will be thrown and caught by the exception handler registered in `RestExceptionHandler.java`.
```java
@ExceptionHandler(value = {ConstraintViolationException.class })
protected ResponseEntity<Object> handleRequestValidationException(ConstraintViolationException ex, WebRequest request) throws Exception {
    String uri = UriUtil.getRequestUri(request);
    StringBuilder message = new StringBuilder();
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    violations.stream().forEach( v -> message.append(v.getMessage()).append("\n"));
    String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.BAD_REQUEST, message.toString(), uri);
    return handleExceptionInternal(ex, bodyOfResponse,
           new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
}
```
A `400` Bad Request error response will be constructed and sent back to client.

### Choice of API Source
After going through many possible relations returned by MusicBrainz API, I decided to use the Discogs API to fetch the profile description information of the artist for the following reason:
* The source provides well documented REST APIs, not all sources provide REST API for 3rd party application to integrate.
* The artist profile description data fetched from this source has very good content. Not all sources provides useful summary for artists in their REST API.
* The Discogs API has a rate limit control to protect itself from high traffic attack. With client requests authenticated, the rate limit is close to the rate limit of MusicBrainz REST service (60 requests per minute) which is acceptable.

This application will issue Discogs API calls with the authorization key and secret strings in the HTTP header, so the request is authenticated.
```java
// DiscogsApiAuthUtil.java
public HttpEntity<String> getAuthHeaderEntity() {
        String authorizationHeader = String.format("Discogs key=%s, secret=%s", apiConfig.getApiDiscogsKey(), apiConfig.getApiDiscogsSecret());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return new HttpEntity<>("parameters", headers);
}
``` 

## API Documentation
When the Spring Boot application is up and running, Swagger UI can be accessed via the URL: `http://<host>:8080/swagger-ui.html`

## Other Remarks
Extra artist mbids can be found in the `artistinfo.feature` cucumber feature file as below:
```
artist id
65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab
5b11f4ce-a62d-471e-81fc-a69a8278c7da
410c9baf-5469-44f6-9852-826524b80c61
```
