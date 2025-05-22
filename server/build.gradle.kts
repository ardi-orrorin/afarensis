import java.util.*

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.hibernate.orm") version "6.6.13.Final"
    id("org.graalvm.buildtools.native") version "0.10.6"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.ardi"
version = "0.0.1-SNAPSHOT"

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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Exec> {
    val npmExecutablePath = "/opt/homebrew/bin"
    val systemPath = System.getenv("PATH")
    environment("PATH", "$npmExecutablePath:$systemPath")
    //환경 변수 추가 해도 npm 못찾는 오류
}


val clientDir = file("$projectDir/../client")
val npmPath = "/opt/homebrew/bin/npm" // npm 실행 파일 경로 (Mac의 경우)


val deleteClientBuild by tasks.registering(Delete::class) {
    delete("$projectDir/src/main/resources/static")
}

val installClient by tasks.registering(Exec::class) {
    val clientDir = file("$projectDir/../client")
    workingDir(clientDir)
    inputs.dir(clientDir)
    group = BasePlugin.BUILD_GROUP

    // OS에 따라 npm 명령어 실행
    if (System.getProperty("os.name").lowercase(Locale.ROOT).contains("windows")) {
        commandLine("npm.cmd", "audit", "fix")
        commandLine("npm.cmd", "i")
    } else {
        commandLine(npmPath, "audit", "fix")
        commandLine(npmPath, "i")
    }
}


val buildClient by tasks.registering(Exec::class) {
    dependsOn(installClient)
    workingDir(clientDir)
    inputs.dir(clientDir)
    group = BasePlugin.BUILD_GROUP

    if (System.getProperty("os.name").lowercase(Locale.ROOT).contains("windows")) {
        commandLine("npm.cmd", "run-script", "build")
    } else {
        commandLine(npmPath, "run", "build")
    }
}

val copyClientBuildFiles by tasks.registering(Copy::class) {
    dependsOn(buildClient)
    from("$clientDir/build")
    into("$projectDir/src/main/resources/static")
}

tasks.named("processResources") {
    dependsOn(copyClientBuildFiles)
}

tasks.named("bootJar") {
    dependsOn(copyClientBuildFiles)
}