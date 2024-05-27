import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.accessors.dm.LibrariesForLibs

val javacppPlatform by extra("windows-x86_64")
val libs = the<LibrariesForLibs>()

plugins {
    java

    // JavaFX
    id("org.openjfx.javafxplugin")

    // gradle-javacpp-platform
    id("org.bytedeco.gradle-javacpp-platform")

    // Static analysis tools
    pmd
    id("com.github.spotbugs")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation(libs.jnativehook)
    implementation(libs.gson)
    implementation(libs.tesseract.platform)

    // -------- Logging --------
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    // -------- Test libraries --------
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    // -------- Spotbugs --------
    // implementation("com.github.spotbugs:spotbugs-annotations:${spotbugs.toolVersion.get()}")
    spotbugs("com.github.spotbugs:spotbugs:${spotbugs.toolVersion.get()}")
    spotbugsPlugins(libs.findsecbugs.plugin)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

javafx {
    version = "22"
}

pmd {
    isIgnoreFailures = false
    toolVersion = "6.55.0"

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

spotbugs {
    excludeFilter.set(file("$rootDir/buildSrc/config/spotbugs/exclude.xml"))
    ignoreFailures.set(false)
    toolVersion.set("4.8.5")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<SpotBugsTask> {
    reports.create("html") {
        required.set(true)
        setStylesheet("fancy-hist.xsl")
    }
}
