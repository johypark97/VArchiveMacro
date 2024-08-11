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
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

javafx {
    version = "21.0.3"
}

pmd {
    isIgnoreFailures = false
    toolVersion = "6.55.0"

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
