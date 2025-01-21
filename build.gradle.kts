plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation("io.quarkiverse.artemis:quarkus-artemis-jms:3.5.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-grpc")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkiverse.quinoa:quarkus-quinoa:2.5.1")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")
    implementation("io.quarkus:quarkus-vertx")
    testImplementation("io.quarkus:quarkus-junit5")
}

group = "amt"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
