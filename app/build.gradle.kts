import java.time.ZonedDateTime
import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask

// distribution settings
val applicationVersion = "1.0.0"
val outFilename = "VArchive Macro"

// build settings
var buildProfile = "development"

plugins {
    // id("com.github.johnrengelman.shadow") version "7.1.2"

    application
    id("edu.sc.seis.launch4j") version "2.5.3"
}

repositories {
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    // testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    // This dependency is used by the application.
    // implementation("com.google.guava:guava:31.0.1-jre")

    implementation("com.github.kwhat:jnativehook:2.2.2")
    implementation("com.google.code.gson:gson:2.9.1")
}

application {
    mainClass.set("com.github.johypark97.varchivemacro.Main")
    mainModule.set("varchivemacro")

    applicationName = outFilename.filter { !it.isWhitespace() }
    executableDir = ""

    // For future use.
    // Include "$projectDir/data" directory in archive files of the distribution task.
    // applicationDistribution.into("data") {
    //     from("data")
    // }
}

// Another way to include the "$projectDir/data" directory.
// distributions.main.get().contents.into("data") {
//     from("data")
// }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// tasks.named<Test>("test") {
//     useJUnitPlatform()
// }

tasks.register<WriteProperties>("processResources_buildProperties") {
    description = "Create a properties file containing build information."

    outputFile = File(sourceSets.main.get().output.resourcesDir, "build.properties")
    property("build.date", ZonedDateTime.now().withNano(0))
    property("build.version", applicationVersion)
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
        attributes["Implementation-Version"] = applicationVersion
        attributes["Main-Class"] = application.mainClass.get()
    }

    archiveBaseName.set("main")
    archiveVersion.set(applicationVersion)
}

// For future use.
// tasks.register<Copy>("copyDataDirectory") {
//     description = "Copy \"\$projectDir/data\" directory to launch4j outputDir."

//     val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
//     from("data")
//     into("$buildDir/${launch4jTask.outputDir}/data")
// }

tasks.withType<DefaultLaunch4jTask> {
    // dependsOn(tasks.named("copyDataDirectory"))

    outfile = "$outFilename v$applicationVersion.exe"

    headerType = "gui"
    icon = "$projectDir/src/main/resources/icon.ico"
    mainClassName = application.mainClass.get()

    copyright = "johypark97"
    fileDescription = rootProject.name
    language = "KOREAN"
    productName = rootProject.name
    textVersion = applicationVersion
    version = applicationVersion

    // copyConfigurable = emptyList<Any>()
    // jarTask = tasks.shadowJar.get()
}

tasks.register<Launch4jLibraryTask>("createExe_localJre") {
    description = "Create an executable using local JRE."
    group = "launch4j"

    outfile = "$outFilename local v$applicationVersion.exe"

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

    archiveBaseName.set(outFilename)
    archiveVersion.set(applicationVersion)

    val launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("$buildDir/${launch4jTask.outputDir}")
    into("$outFilename v$applicationVersion")
}
