import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask

val applicationVersion = "1.0.0"
val outFilename = "VArchive Macro"

var buildProfile = "development"

plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// tasks.named<Test>("test") {
//     useJUnitPlatform()
// }

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = applicationVersion
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.withType<DefaultLaunch4jTask> {
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

    copyConfigurable = emptyList<Object>()
    jarTask = tasks.shadowJar.get()
}

tasks.register<Launch4jLibraryTask>("createExe_localJre") {
    description = "Create an executable using local JRE"
    group = "launch4j"

    outfile = "$outFilename local v$applicationVersion.exe"

    bundledJrePath = "jre17"
}

tasks.processResources {
    dependsOn(tasks.named("copyResources_buildProfile"))
}

tasks.register<Copy>("copyResources_buildProfile") {
    description = "Copy resources located in resources-(buildProfile)"

    from("src/main/resources-$buildProfile")
    into("$buildDir/resources/main")
}

tasks.register("run_production") {
    dependsOn(tasks.run)
    description = "Run with production profile"
    group = "application"

    buildProfile = "production"
}

tasks.register("createAllExecutables_production") {
    dependsOn(tasks.createAllExecutables)
    description = "Create all executables with production profile"
    group = "launch4j"

    buildProfile = "production"
}

tasks.register<Zip>("releaseProduction") {
    dependsOn(tasks.named("createAllExecutables_production"))
    description = "Create an archive file to release"
    group = "distribution"

    archiveBaseName.set(outFilename)
    archiveVersion.set(applicationVersion)

    var launch4jTask = tasks.named<DefaultLaunch4jTask>("createExe").get()
    from("$buildDir/${launch4jTask.outputDir}")
    into("$outFilename v$applicationVersion")
}
