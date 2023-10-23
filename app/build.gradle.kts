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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.data:spring-data-redis:3.0.0")
    implementation("redis.clients:jedis:4.4.3")
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
}

tasks.withType<Test> {
    useJUnitPlatform()
}
