import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
val mockitoAgent: Configuration = configurations.create("mockitoAgent")

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
    implementation(libs.rxjava)
    implementation(libs.sqlite.jdbc)

    // -------- Logging --------
    implementation(libs.log4j.slf4j2.impl)
    implementation(libs.slf4j.api)

    // -------- Test libraries --------
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    @Suppress("UnstableApiUsage") mockitoAgent(libs.mockito.core) { isTransitive = false }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
    version = libs.versions.javafx.version.get()
}

pmd {
    isIgnoreFailures = false
    toolVersion = libs.versions.pmd.get()

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

tasks.named<Test>("test") {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
}
