import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

val appName = "VArchive Macro"
val buildVersion = Version.makeVersionString()

var isDev = true

plugins {
    id("buildlogic.java-application-conventions")

    // id("com.github.johnrengelman.shadow") version "8.1.1"
    alias(libs.plugins.launch4j)
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

    applicationName = appName
    executableDir = ""

    // Include '$projectDir/data' directory in archive files of the distribution task.
    applicationDistribution.into("data") {
        from("data")
    }

    applicationDistribution.into("") {
        from("${rootProject.projectDir}/LICENSE")
        from("${rootProject.projectDir}/README.md")
    }

    applicationDistribution.into("doc") {
        from("${rootProject.projectDir}/doc")
    }
}

// Another way to include the '$projectDir/data' directory.
// distributions.main.get().contents.into("data") {
//     from("data")
// }

tasks.register<WriteProperties>("processResources_buildProperties") {
    description = "Create a properties file containing build information."

    destinationFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
    property("build.version", buildVersion)
}

tasks.register<Copy>("processResources_resourcesDev") {
    description = "Copy 'resource-dev' directory."

    if (isDev) {
        from("src/main/resources-dev")
        into("${layout.buildDirectory.get()}/resources/main")
    }
}

tasks.processResources {
    dependsOn("processResources_buildProperties")
    dependsOn("processResources_resourcesDev")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName = "macro"
    archiveVersion = buildVersion
}

tasks.register<Copy>("copyDataDirToLaunch4j") {
    description = "Copy '\$projectDir/data' directory to launch4j outputDir."

    from("data")
    into("${layout.buildDirectory.get()}/${launch4j.outputDir.get()}/data")
}

tasks.register<Copy>("copyLicenseAndReadmeToLaunch4j") {
    description = "Copy the LICENSE and README files to launch4j outputDir."

    from("${rootProject.projectDir}/LICENSE")
    from("${rootProject.projectDir}/README.md")
    into("${layout.buildDirectory.get()}/${launch4j.outputDir.get()}")
}

tasks.register<Copy>("copyDocDirToLaunch4j") {
    description = "Copy '\${rootProject.projectDir}/doc' directory to launch4j outputDir."

    from("${rootProject.projectDir}/doc")
    into("${layout.buildDirectory.get()}/${launch4j.outputDir.get()}/doc")
}

tasks.withType<DefaultLaunch4jTask> {
    dependsOn("copyDataDirToLaunch4j")
    dependsOn("copyDocDirToLaunch4j")
    dependsOn("copyLicenseAndReadmeToLaunch4j")

    outfile = "$appName v$buildVersion.exe"

    headerType = "gui"
    icon = "$projectDir/src/main/resources/icon.ico"
    jvmOptions = setOf("-Dfile.encoding=UTF-8")
    mainClassName = application.mainClass.get()

    copyright = "johypark97"
    fileDescription = rootProject.name
    language = "KOREAN"
    productName = rootProject.name
    textVersion = buildVersion
    version = buildVersion

    // copyConfigurable = emptyList<Any>()
    // jarTask = tasks.shadowJar.get()
}

tasks.register<Launch4jLibraryTask>("createExe_envJre") {
    description = "Create an executable using JAVA_HOME env JRE."
    group = "launch4j"

    outfile = "$appName env v$buildVersion.exe"

    bundledJrePath = "%JAVA_HOME%"
}

tasks.register<Launch4jLibraryTask>("createExe_localJre") {
    description = "Create an executable using local JRE."
    group = "launch4j"

    outfile = "$appName local v$buildVersion.exe"

    bundledJrePath = "jre17fx"
}

tasks.register<Zip>("release") {
    dependsOn(tasks.createAllExecutables)
    description = "Create an archive file to release."
    group = "distribution"

    archiveBaseName = appName
    archiveVersion = buildVersion

    isDev = false

    from("${layout.buildDirectory.get()}/${launch4j.outputDir.get()}")
    into("$appName v$buildVersion")
}
