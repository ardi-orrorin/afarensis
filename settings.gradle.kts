plugins {
    id("com.github.node-gradle.node") version "7.1.0" apply false
}

rootProject.name = "afarensis"

include("client", "server")