import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask

val applicationVersion = "0.0.0"
val outFilename = "out"

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
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.withType<DefaultLaunch4jTask> {
    outfile = "$outFilename v$applicationVersion.exe"

    headerType = "gui"
    mainClassName = application.mainClass.get()

    jarTask = tasks.shadowJar.get()
}
