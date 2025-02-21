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
    implementation(libs.log4j.slf4j2.impl)
    implementation(libs.slf4j.api)

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
    version = libs.versions.javafx.version.get()
}

pmd {
    isIgnoreFailures = false
    toolVersion = libs.versions.pmd.get()

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
