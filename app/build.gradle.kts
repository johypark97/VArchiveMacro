import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask

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
        languageVersion.set(JavaLanguageVersion.of(11))
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

    jarTask = tasks.shadowJar.get()
}

tasks.processResources {
    dependsOn(tasks.named("copyResources-buildProfile"))
}

tasks.register<Copy>("copyResources-buildProfile") {
    description = "Copy resources located in resources-(buildProfile)"

    from("src/main/resources-$buildProfile")
    into("$buildDir/resources/main")
}

tasks.register("runProduction") {
    dependsOn(tasks.run)

    description = "Run with production profile"
    group = "application"

    buildProfile = "production"
}

tasks.register("createProductionExe") {
    dependsOn(tasks.createExe)

    description = "Create an executable with production profile"
    group = "launch4j"

    buildProfile = "production"
}
