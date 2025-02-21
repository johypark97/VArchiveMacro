import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

version = findProperty("version") as String

plugins {
    id("buildlogic.java-application-conventions")
    id("buildlogic.java-javacpp-conventions")
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

    applicationDefaultJvmArgs += "-Dlog.level=ALL"
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = application.mainClass
    }

    archiveBaseName = "dbmanager"
    archiveVersion = version.toString()
}

tasks.processResources {
    dependsOn("buildProperties")
}

tasks.register<WriteProperties>("buildProperties") {
    description = "Creates a properties file containing the build information."

    destinationFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
    property("build.version", version)
}
