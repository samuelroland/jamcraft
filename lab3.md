# Labo3

In this lab the focus is on developing a multi-tier application, including frontend, backend, and database components. The primary goal of this lab is to deploy a fully functional application. A *new* technology not explored in previous assignments or in the course has to be chosen and employed in the application.

Here are the mandatory deliverables of the lab:
* The initial report
* The whole application code and data
* A presentation for the *new* technology chosen

All the above deliverables are delivered by adding them to the same repository for each deadline communicated in course.
The presentation slides are to be added to the repository and will be presented in course.

The progress made will be inquired randomly during the lab's sessions. It is expected that group work independently and can demonstrate their progress iteratively. Groups that are unable to demonstrate progress on the lab will receive penalties on their grading.

## Initial report requirements
* A summary describing the purpose and business domain of the application.
* Several (i.e. between 3 and 10) user stories that the application will provide. Template: 
“As [a persona], I want [to perform this action] so that [I can accomplish this goal].”
* The chosen *new* technology that will be employed and presented by the group at the end of the lab.
* Prioritize features with high added value and creativity.
* The following diagrams:
    * Architecture, including main components, their relations, including the messaging flow.
    * Data model of the domain, the focus is on the main entities and relations. The diagram will likely evolve during the implementation.

## Application requirements
* The application has to be a multi-tiers application with a frontend, backend and database.
* The backend is implemented in Java and a dependency manager (e.g., Maven, Gradle)
* JMS (Java Message Service) messaging has to be used for at least one feature within the backend.
* A *new* technology is meaningfully used in the application.
* The application must offer several cases of data reads and writes.
* Unit tests and integration tests for core features within the perimeter of the backend application.


## Repository requirements
* A README file is included that describes the project.
* The README includes instructions how to run locally the whole application for development purposes and its dependencies.
* The repository is self-contained and assumes little about the developer host system. Any required dependency has to be described in the README. The following elements can be expected from the host system:
    * Java 21
    * Maven or Gradle
    * Container engine (Docker CLI compatible)
* A folder `docs` at the root that will contain the initial report and the final presentation.
* The repository must be well-structured and easy to navigate.

## Presentation requirements
The presentation of the *new* technology must include:
* An explanation of the tool, its intended purpose, and the method by which it fulfills its function.
* The pros and cons of the tool as experienced during its usage.
* A detailed feedback from the experience of using the tool.
* Demonstrate how it was concretely integrated into the application, with examples or demo.

## *New* technology
The goal is to explore a *new* technology and achieve concrete results with it. The technology must be employed meaningfully in the final delivered application.

Examples of themes and technologies that could be selected (in no particular order).
Ideas and propositions outside of this list can be discussed.

* LLM and their integration in multitier application
  * https://github.com/langchain4j/langchain4j
  * https://github.com/deepjavalibrary/djl
* New paradigm for web applications
  * https://vaadin.com/
  * https://vaadin.com/hilla
  * https://github.com/grpc/grpc-web
  * https://graphql.org/
* Monitoring and observability tracing
  * https://zipkin.io/
  * https://opentelemetry.io/
  * Loki, Grafana
  * Structured logging
* Micro-services
  * https://spring.io/projects/spring-cloud
  * https://www.graalvm.org/ and native images
* Code generation
  * https://github.com/palantir/javapoet
  * https://projectlombok.org/
  * https://github.com/OpenAPITools/openapi-generator
  * https://github.com/microsoft/kiota-java 
  * https://mapstruct.org/
* Test automation
  * https://site.mockito.org/
  * https://wiremock.org/
  * https://cucumber.io/
  * https://java.testcontainers.org/
* Caching (and eviction strategies)
  * https://infinispan.org/
  * https://redis.io
  * https://github.com/ben-manes/caffeine