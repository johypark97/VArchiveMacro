import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

val appName = "VArchive Macro"

var isRelease = false

version = findProperty("version") as String

plugins {
    id("buildlogic.java-application-conventions")
    id("buildlogic.java-javacpp-conventions")

    alias(libs.plugins.jlink.plugin)
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

jlink {
    options = listOf("--no-header-files", "--no-man-pages")

    // slf4j - log4j2
    addExtraDependencies("log4j-api")
    addExtraDependencies("log4j-core")
    addExtraDependencies("log4j-slf4j2-impl")
    addExtraDependencies("slf4j-api")

    mergedModule {
        additive = true

        // Fixes javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure.
        requires("jdk.crypto.ec")

        // slf4j - log4j2
        run {
            requires("java.naming") // Fixes the JNDI lookup class warning.

            requires("org.apache.logging.log4j.core")
            requires("org.apache.logging.log4j.slf4j2.impl")
        }
    }

    launcher {
        name = "macro v${version}"
    }

    jpackage {
        icon = "jlink/icon.ico"
    }
}

tasks.jar.get().doLast {
    if (!isRelease) application.applicationDefaultJvmArgs += "-Dlog.level=ALL"
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

tasks.jpackageImage.get().doLast {
    val jPackageData = jlink.jpackageData.get()
    val jPackageImageDir = "${jPackageData.imageOutputDir}/${jPackageData.imageName}"

    copy {
        from("${rootProject.projectDir}/LICENSE")
        from("${rootProject.projectDir}/README.md")
        into(jPackageImageDir)
    }

    copy {
        from("${rootProject.projectDir}/doc")
        into("$jPackageImageDir/doc")
    }

    copy {
        from("data")
        into("$jPackageImageDir/data")
    }
}

tasks.register<Zip>("release") {
    description = "Creates an archive file to release."
    group = "distribution"

    dependsOn(tasks.jpackageImage)

    isRelease = true

    archiveBaseName = appName
    archiveVersion = version.toString()

    val jPackageData = jlink.jpackageData.get()
    from("${jPackageData.imageOutputDir}/${jPackageData.imageName}")
    into("$appName v$version")
}
