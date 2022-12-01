import java.time.ZonedDateTime

val appName = "DB Manager"
val buildBasename = "dbmanager"
val buildVersion = "1.0.0"

plugins {
    id("project.java-application-conventions")
}

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:hook"))
    implementation(project(":lib:json"))
}

application {
    mainClass.set("com.github.johypark97.varchivemacro.dbmanager.Main")
    mainModule.set("varchivemacro.dbmanager")

    applicationName = appName
    executableDir = ""
}

tasks.register<WriteProperties>("processResources_buildProperties") {
    description = "Create a properties file containing build information."

    outputFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().withNano(0))
    property("build.version", buildVersion)
}

tasks.processResources {
    dependsOn(tasks.named("processResources_buildProperties"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
