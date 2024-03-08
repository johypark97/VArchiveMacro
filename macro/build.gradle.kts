import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import java.time.ZonedDateTime

val appName = "VArchive Macro"
val buildBasename = "macro"
val buildVersion = Version.makeVersionString()

var isDev = true

plugins {
    id("project.java-application-conventions")

    // id("com.github.johnrengelman.shadow") version "7.1.2"
    id("edu.sc.seis.launch4j") version "2.5.4"
}

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:jfx"))
    implementation(project(":lib:scanner"))
}

application {
    mainClass.set("com.github.johypark97.varchivemacro.macro.Main")
    mainModule.set("varchivemacro.macro")

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

    outputFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().withNano(0))
    property("build.version", buildVersion)
}

tasks.register<Copy>("processResources_resourcesDev") {
    description = "Copy 'resource-dev' directory."

    if (isDev) {
        from("src/main/resources-dev")
        into("$buildDir/resources/main")
    }
}

tasks.processResources {
    dependsOn(tasks.named("processResources_buildProperties"))
    dependsOn(tasks.named("processResources_resourcesDev"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}

tasks.register<Copy>("copyDataDirToLaunch4j") {
    description = "Copy '\$projectDir/data' directory to launch4j outputDir."

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("data")
    into("$buildDir/${launch4jTask.outputDir}/data")
}

tasks.register<Copy>("copyLicenseAndReadmeToLaunch4j") {
    description = "Copy the LICENSE and README files to launch4j outputDir."

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("${rootProject.projectDir}/LICENSE")
    from("${rootProject.projectDir}/README.md")
    into("$buildDir/${launch4jTask.outputDir}")
}

tasks.register<Copy>("copyDocDirToLaunch4j") {
    description = "Copy '\${rootProject.projectDir}/doc' directory to launch4j outputDir."

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("${rootProject.projectDir}/doc")
    into("$buildDir/${launch4jTask.outputDir}/doc")
}

tasks.withType<DefaultLaunch4jTask> {
    dependsOn(tasks.named("copyDataDirToLaunch4j"))
    dependsOn(tasks.named("copyDocDirToLaunch4j"))
    dependsOn(tasks.named("copyLicenseAndReadmeToLaunch4j"))

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

    bundledJrePath = "jre17"
}

tasks.register<Zip>("release") {
    dependsOn(tasks.createAllExecutables)
    description = "Create an archive file to release."
    group = "distribution"

    archiveBaseName.set(appName)
    archiveVersion.set(buildVersion)

    isDev = false

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("$buildDir/${launch4jTask.outputDir}")
    into("$appName v$buildVersion")
}
