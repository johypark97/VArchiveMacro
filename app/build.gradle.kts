import java.time.ZonedDateTime
import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask

val appName = "VArchive Macro"
val buildBasename = "app"
val buildVersion = "1.0.0"

var buildProfile = "development"

plugins {
    id("project.java-application-conventions")

    // id("com.github.johnrengelman.shadow") version "7.1.2"
    id("edu.sc.seis.launch4j") version "2.5.4"
    id("org.bytedeco.gradle-javacpp-platform") version("1.5.8")
}

dependencies {
    implementation(project(":lib:hook"))
    implementation(project(":lib:json"))

    implementation("org.bytedeco:tesseract-platform:5.2.0-1.5.8")
}

application {
    mainClass.set("com.github.johypark97.varchivemacro.Main")
    mainModule.set("varchivemacro.app")

    applicationName = appName
    executableDir = ""

    // Include "$projectDir/data" directory in archive files of the distribution task.
    applicationDistribution.into("data") {
        from("data")
    }
}

// Another way to include the "$projectDir/data" directory.
// distributions.main.get().contents.into("data") {
//     from("data")
// }

tasks.register<WriteProperties>("processResources_buildProperties") {
    description = "Create a properties file containing build information."

    outputFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().withNano(0))
    property("build.version", buildVersion)
}

tasks.register<Copy>("processResources_res_buildProfile") {
    description = "Copy resource files located in \"resources-\$buildProfile\" directory."

    from("src/main/resources-$buildProfile")
    into(sourceSets.main.get().output.resourcesDir!!)
}

tasks.processResources {
    dependsOn(tasks.named("processResources_buildProperties"))
    dependsOn(tasks.named("processResources_res_buildProfile"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}

tasks.register<Copy>("copyDataDirectory") {
    description = "Copy \"\$projectDir/data\" directory to launch4j outputDir."

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("data")
    into("$buildDir/${launch4jTask.outputDir}/data")
}

tasks.withType<DefaultLaunch4jTask> {
    dependsOn(tasks.named("copyDataDirectory"))

    outfile = "$appName v$buildVersion.exe"

    headerType = "gui"
    icon = "$projectDir/src/main/resources/icon.ico"
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

tasks.register<Launch4jLibraryTask>("createExe_localJre") {
    description = "Create an executable using local JRE."
    group = "launch4j"

    outfile = "$appName local v$buildVersion.exe"

    bundledJrePath = "jre17"
}

tasks.register("run_production") {
    dependsOn(tasks.run)
    description = "Run with production profile."
    group = "application"

    buildProfile = "production"
}

tasks.register("createAllExecutables_production") {
    dependsOn(tasks.createAllExecutables)
    description = "Create all executables with production profile."
    group = "launch4j"

    buildProfile = "production"
}

tasks.register<Zip>("releaseProduction") {
    dependsOn(tasks.named("createAllExecutables_production"))
    description = "Create an archive file to release."
    group = "distribution"

    archiveBaseName.set(appName)
    archiveVersion.set(buildVersion)

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("$buildDir/${launch4jTask.outputDir}")
    into("$appName v$buildVersion")
}
