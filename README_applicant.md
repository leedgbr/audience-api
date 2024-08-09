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
    }

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

## Notes

* If time allows, create an openapi spec definition to describe the Audience API contract. This serves as useful
  documentation for integrating engineers and also allows for the possibility of generating a client library in various
  languages.