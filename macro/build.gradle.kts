import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

val appName = "VArchive Macro"

version = Version.makeVersionString()

plugins {
    id("buildlogic.java-application-conventions")
    id("buildlogic.java-javacpp-conventions")

    alias(libs.plugins.launch4j.plugin)
}

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:desktop"))
    implementation(project(":lib:hook"))
    implementation(project(":lib:jfx"))
    implementation(project(":lib:scanner"))
}

application {
    mainClass = "com.github.johypark97.varchivemacro.macro.Main"
    mainModule = "varchivemacro.macro"
}

tasks.jar.get().doLast {
    application.applicationDefaultJvmArgs += "-Dlog.level=ALL"
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

tasks.withType<DefaultLaunch4jTask> {
    outfile = "$appName v${project.version}.exe"

    headerType = "gui"
    icon = "$projectDir/src/main/resources/icon.ico"
    jvmOptions = application.applicationDefaultJvmArgs.toSet()
    mainClassName = application.mainClass.get()

    copyright = "johypark97"
    fileDescription = rootProject.name
    language = "KOREAN"
    productName = rootProject.name
    textVersion = project.version.toString()
    version = project.version.toString()

    doLast {
        val path = "${layout.buildDirectory.get()}/${launch4j.outputDir.get()}"

        copy {
            from("${rootProject.projectDir}/LICENSE")
            from("${rootProject.projectDir}/README.md")
            into(path)
        }

        copy {
            from("data")
            into("$path/data")
        }

        copy {
            from("${rootProject.projectDir}/doc")
            into("$path/doc")
        }
    }
}

tasks.register<Launch4jLibraryTask>("createExe_envJre") {
    description = "Creates an executable using JAVA_HOME env JRE."
    group = "launch4j"

    outfile = "$appName env v${project.version}.exe"

    bundledJrePath = "%JAVA_HOME%"
}

tasks.register<Launch4jLibraryTask>("createExe_localJre") {
    description = "Creates an executable using local JRE."
    group = "launch4j"

    outfile = "$appName local v${project.version}.exe"

    bundledJrePath = "jre17fx"
}

tasks.register<Zip>("release") {
    description = "Creates an archive file to release."
    group = "distribution"

    dependsOn(tasks.createAllExecutables)

    archiveBaseName = appName
    archiveVersion = version.toString()

    from("${layout.buildDirectory.get()}/${launch4j.outputDir.get()}")
    into("$appName v$version")
}
