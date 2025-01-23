# Description of the application

The application is a web application allowing many users at the same time to collaborate on a music mix creation. If we think about something well known, we could say that the application is a simplified collaborative version of GarageBand. 

## Introduction

TODO: add screenshot with a nice audio timeline

## Development setup
### Requirements
- Docker
- Java
- FFmpeg (on MacOS `brew install ffmpeg`, otherwise see [download page](https://www.ffmpeg.org/download.html))

### Setup
**Clone or get the project**
```sh
git clone git@github.com:amt-classroom/amtb-lab3-amtb-ouadahi-roland-strcksn-vanhove.git
cd amtb-lab3-amtb-ouadahi-roland-strcksn-vanhove
```

**Run the Envoy proxy**

On MacOS and Linux, this command
```sh
docker run -p 8081:8080 -p 9091:9091 -v ./envoy.yaml:/etc/envoy/envoy.yaml envoyproxy/envoy:v1.17.0
```
On Windows, Powershell requires `\`
```sh
docker run -p 8081:8080 -p 9091:9091 -v .\envoy.yaml:/etc/envoy/envoy.yaml envoyproxy/envoy:v1.17.0
```

**Start the quarkus dev server**

Using the Quarkus CLI
```sh
quarkus dev
```

TODO document quarkus.http.host=0.0.0.0
TODO document proxy IP to change
TODO: make sure to empty the import.sql or comment it

Using the Gradle wrapper otherwise
```sh
./gradlew quarkusDev
```

**Open your browser on `localhost:8080`**

You should see the UI loaded with an empty library and no track, that's normal.

TODO: add screenshot with empty UI

You can take a few audios file in MP3 formats (note for teachers and assistants: take the zip we gave you). You can find way more public domain sounds on `freesound.org` in case you want to try it more.

See usage on how to learn how to use it.

<!--Test the entire setup-->

## Usage
1. Login: choose a username for the session, this will be persisted locally to support page refresh
<!-- 1. Leave : TODO does it work ?? -->
1. Upload new samples
    1. Try to drag and drop a MP3 file
    1. You can also automate the upload with this Fish function (if you use Fish)
    ```fish
    function jamcraft_upload
        for i in $argv
            echo "Uploading $i"
            curl -s -X POST -F name=(string sub -e 25 (string split __ $i)[2]) -F file=@$i localhost:8080/samples | jq
            echo ""
        end
    end
    ```
    1. You can find uploaded files in the `audio` folder at repository's root
1. Try to play and see the waveforms of audio in the library on the left
<!--TODO: screenshot :)-->
1. Add new tracks by drag and dropping samples from the library to the dedicated zone at bottom right.
<!--TODO: screenshot :)-->
1. Load the project timeline by pressing twice on `Load project`
<!--TODO: screenshot :)-->
TODO: continue !!



**Generated README to sort**

----

# jamcraft

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/jamcraft-0.1.0-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- Quinoa ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-quinoa/dev/index.html)): Develop, build, and serve your npm-compatible web applications such as React, Angular, Vue, Lit, Svelte, Astro, SolidJS, and others alongside Quarkus.

## Provided Code

### gRPC

Create your first gRPC service

[Related guide section...](https://quarkus.io/guides/grpc-getting-started)

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### Quinoa

Quinoa codestart added a tiny Vite app in src/main/webui. The page is configured to be visible on <a href="/quinoa">/quinoa</a>.

[Related guide section...](https://quarkiverse.github.io/quarkiverse-docs/quarkus-quinoa/dev/index.html)

