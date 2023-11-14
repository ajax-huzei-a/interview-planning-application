plugins {
    kotlin("jvm") version "1.9.0"
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    id("io.gitlab.arturbosch.detekt") version("1.23.1")
}

val kotlinVersion = "1.9.0"

dependencies {
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.nats:jnats:2.16.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation(project(":internal-api"))

    implementation("org.springframework.kafka:spring-kafka:2.9.1")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("io.projectreactor:reactor-core:3.4.22")

    implementation("io.confluent:kafka-schema-registry-maven-plugin:7.5.1")
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
