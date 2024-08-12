# Audition API

This service provides an api for accessing audition posts. These posts are stored in another system. This service
adapts the interface of that system into a more user-friendly interface with enhanced validation and filtering.

## Context

                    ┌───────────────────┐                  ┌───────────────────┐
                    │                   │                  │   Audition Post   │
    ──────────────► │   Audition API    │ ───────────────► │                   │
                    │                   │                  │  (source system)  │
                    └───────────────────┘                  └───────────────────┘

## Dependencies

* Java SDK 17 (consider installing via [SDKMAN](https://sdkman.io/))

## Getting Started

Run Tests

    ./gradlew test

Run checks

    ./gradlew check

Start service

    ./gradlew bootRun

## Call API

### Fetch posts

    curl http://localhost:8080/posts

Sample response

    [
      {
        "userId": 1,
        "id": 1,
        "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
        "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
      },
      ...
    ]

### Fetch post by id

    curl http://localhost:8080/posts/65

Sample response

    {
      "userId": 1,
      "id": 65,
      "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
      "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
      "comments": [
        {
          "id": 321,
          "name": "rem ducimus ipsam ut est vero distinctio et",
          "email": "Danyka_Stark@jedidiah.name",
          "body": "ea necessitatibus eum nesciunt corporis\nminus in quisquam iste recusandae\nqui nobis deleniti asperiores non laboriosam sunt molestiae dolore\ndistinctio qui officiis tempora dolorem ea"
        },
        ...
      ]
    }

### Fetch comments by post id

    curl http://localhost:8080/posts/65/comments

Sample response

    [
      {
        "id": 321,
        "name": "rem ducimus ipsam ut est vero distinctio et",
        "email": "Danyka_Stark@jedidiah.name",
        "body": "ea necessitatibus eum nesciunt corporis\nminus in quisquam iste recusandae\nqui nobis deleniti asperiores non laboriosam sunt molestiae dolore\ndistinctio qui officiis tempora dolorem ea"
      },
      ...
    ]

### Metrics

#### Health

    curl http://localhost:8080/actuator/health

Sample response

    {
      "status": "UP"
    }

#### Info

    curl http://localhost:8080/actuator/info

Sample response

    {
      "app": {
        "details": "this is really useful detail"
      }
    }

## Assumptions

* The Audition API has been created to overcome the constraints of the system from which Audition Posts are sourced.
  These constraints may be that the source system is inaccessible to the expected clients of the Audition API, or maybe
  that the source system cannot be modified yet there is a need for additional functionality around accessing Audition
  Posts.
* The Audition source system contract states that it will respond with http status 200 for success. We don't expect it
  to return 201, or any other 2xx success statuses.
* There is no specific requirement to expose details about system errors to clients.
* There is a user scenario that requires a list of post comments without the post details.
* The source system is a trusted system that does not return anything crazy that we shouldn't be returning back to
  clients.
* The source system filtering of posts by user behaves sensibly and as we would expect.

## Notes

* If time allows, create an openapi spec definition to describe the Audience API contract. This serves as useful
  documentation for integrating engineers and also allows for the possibility of generating a client library in various
  languages.
* Exception handling has been refactored to reduce / remove the risk of exposing internal details of the system. This
  may or may not be controversial - or against expected requirements. Details of errors that the caller can remediate
  (i.e. business errors or those due to a malformed request) are exposed in the api response. Errors the client is not
  able to remediate result in a standard api response message that exposes no system details. Only those errors the
  client is able to remediate are logged. Happy to discuss this approach if it is at odds with what is expected.
* I have kept exception handling light for now. Could decide on error wrapping strategy, perhaps at certain layers, if
  deemed worthwhile.
* Have not had time, but consider separating the highest level tests (i.e. `ActuatorEndpointTests` and
  `AuditionApplicationTests`) into their own gradle test target and exclude them from the default unit test run. This
  will help provide more accurate granular unit test coverage data and improve granular unit test cycle times.
* Without knowledge of the domain it's not clear if there are privacy concerns around which users may access which user'
  s audition posts / comments. Needs clarifying with product.
* Both methods of fetching back comments gave the same response.
  i.e. https://jsonplaceholder.typicode.com/comments?postId={postId}
  and https://jsonplaceholder.typicode.com/posts/{postId}/comments . It seems they are different methods of retrieving
  the same results, therefore I have wired up only one of them. It looks like these are endpoint stylistic differences.
  The limited documentation provides no further clues. In the real world, this may warrant double-checking behaviour of
  these endpoints with an SME. There may be some data I haven't checked that exhibits a difference in behaviour between
  the two ways of fetching comments. It's not currently warranted to do an exhaustive check of the data, and in a
  real situation this may be impossible due to volume.
* The simplest possible solution I can think of (around fetching posts / comments) involves the following two user
  scenarios.. 1) view all posts (without comments), 2) view a single post with comments. Given this, at the point the
  user wants to view a single post with comments the client already has the post details, and so would not need them
  fetched again. The client could simply fetch the comments for the post and display this with the post details they
  already have. Additionally, there is a performance impact fetching a post and it's comments together because we need
  to make two separate calls to get each of those details - so it would be great if we didn't need to support this. It
  is possible that there may be another user scenario where a user may have 'favourited' a post, in which case we may
  need to fetch the post and the comments together. However, prior to implementing support for this user scenario, it
  would be worth a conversation with product and possibly the wider team, to make sure we fully understand the possible
  user scenarios and are all on the same page. For the purposes of this exercise I have supported all three user
  scenarios described.
* Noticed that `com.audition.model.AuditionPost` in the post list now contains a `Comment` list, however that is not (
  currently) in sync with what we return from the list posts endpoint. Currently, the one model is used for
  communication with the source system and for what the audition api returns. If the model becomes more complex, it may
  make sense decouple these so that it remains easy to understand the codebase.
* One of the TODO comments mentioned adding a filter to the `/posts/` operation. A useful filter might be to filter
  the results by user. It turns out that this is implemented by the source system, which means we are able to delegate
  the implementation of this feature. Provided there are no really specific requirements around filtering this should be
  sufficient.
* When time allows, add environment specific configuration to ensure the service is correctly configured for the
  environment it is deployed to.
* Source system data has been stubbed locally for the more granular tests. The provides flexibility, with only the
  coarse grained tests directly wired to the remote source system endpoint. Depending on preferences of the team,
  various different strategies could be used for sourcing and setting up test data.
* An environment variable `SECURITY_PASSWORD` is used to set the default spring security password when the application
  starts up. Spring Security has been enabled in order to restrict access to various endpoints while allowing the
  possibility of allowing access to some authenticated users at a later date when authentication has been implemented.
  Setting the `SECURITY_PASSWORD` environment variable means that a default password is not generated and displayed in
  the logs, which would present a security risk in production. When this services is deployed to production, an
  appropriately secure password should be set in this environment variable to ensure no one is able to access the
  protected endpoints. This is a very basic level of security implementable in the time available, would suggest
  improving on this when time allows.