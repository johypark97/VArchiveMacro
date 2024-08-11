import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

val buildVersion = Version.makeVersionString()

plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:desktop"))
    implementation(project(":lib:hook"))
    implementation(project(":lib:jfx"))
    implementation(project(":lib:scanner"))
}

application {
    mainClass = "com.github.johypark97.varchivemacro.dbmanager.Main"
    mainModule = "varchivemacro.dbmanager"

    applicationName = "DB Manager"
    executableDir = ""
}

tasks.register<WriteProperties>("processResources_buildProperties") {
    description = "Create a properties file containing build information."

    destinationFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
    property("build.version", buildVersion)
}

tasks.processResources {
    dependsOn("processResources_buildProperties")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName = "dbmanager"
    archiveVersion = buildVersion
}
