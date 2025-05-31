plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.hibernate.orm") version "6.6.13.Final"
    id("org.graalvm.buildtools.native") version "0.10.6"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.github.node-gradle.node")
}

group = "com.ardi"
version = project.properties["version"] ?: "dev-version"

java {
    toolchain {
//        languageVersion = JavaLanguageVersion.of(24) // 23까지만 지원
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"

dependencies {
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

hibernate {
    enhancement {
        enableAssociationManagement = true
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

val staticResourceDir = "src/main/resources/static"

val deletePreviousClientBuild by tasks.registering(Delete::class) {
    group = "build"
    delete(file(staticResourceDir))
}

val copyClientBuildToStaticResources by tasks.registering(Copy::class) {
    group = "build"

    dependsOn(deletePreviousClientBuild)

    val clientBuild = project(":client")
        .tasks.named<com.github.gradle.node.npm.task.NpmTask>("buildClient")

    clientBuild.configure {
        environment.put("BUILD", System.getenv("BUILD") ?: "DEV")
    }

    dependsOn(clientBuild)

    from(project(":client").file("build"))
    into(file(staticResourceDir))
}

tasks.named("processResources") {
    dependsOn(copyClientBuildToStaticResources)
}

tasks.named("bootJar") {
    dependsOn(copyClientBuildToStaticResources)
}


tasks.named("build") {
    dependsOn(copyClientBuildToStaticResources)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}