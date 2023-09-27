import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.1.3"
    id ("org.jetbrains.kotlin.plugin.spring") version "1.9.0"
    id("io.gitlab.arturbosch.detekt") version("1.23.1")
}

repositories {
    mavenCentral()
}

val kotlinVersion = "1.9.0"

group = "com.intellias.intellistart"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.data:spring-data-redis:3.0.0")
    implementation("redis.clients:jedis:4.3.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
}

springBoot {
    mainClass.set("intellistart.interviewplanning.InterviewPlanningApplication")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
