plugins {
    id("base")
    id("com.github.node-gradle.node")
}

node {
    version.set("23.11.0")
    download.set(true)
}

//val npmAuditFix by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
//    description = "npm audit fix --force"
//    group = "build"
//    npmCommand.set(listOf("audit", "fix", "--force"))
//}

tasks.named<com.github.gradle.node.npm.task.NpmInstallTask>("npmInstall") {
    group = BasePlugin.BUILD_GROUP
//    dependsOn(npmAuditFix)
}


val buildClient by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    group = BasePlugin.BUILD_GROUP

    npmCommand.set(listOf("run", "build"))

    dependsOn(tasks.named("npmInstall"))

    inputs.dir(project.projectDir)

    outputs.dir(layout.buildDirectory.dir("dist"))
}

tasks.named("assemble") {
    dependsOn(buildClient)
}
