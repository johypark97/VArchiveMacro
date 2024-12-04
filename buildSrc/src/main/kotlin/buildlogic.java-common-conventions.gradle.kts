import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    java

    // JavaFX
    id("org.openjfx.javafxplugin")

    // Static analysis tools
    pmd
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.jnativehook)
    implementation(libs.sqlite.jdbc)

    // -------- Logging --------
    implementation(libs.slf4j.api)
    implementation(libs.log4j.slf4j2.impl)

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
    version = "21.0.4"
}

pmd {
    isIgnoreFailures = false
    toolVersion = "7.4.0"

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
