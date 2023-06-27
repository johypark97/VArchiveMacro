import com.github.spotbugs.snom.SpotBugsTask

val javacppPlatform by extra("windows-x86_64")

plugins {
    java

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
    implementation("com.google.guava:guava:31.0.1-jre")

    // -------- Logging --------
    implementation("org.slf4j:slf4j-api:2.0.6")

    // implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // -------- Test libraries --------
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.11.0")

    // -------- Spotbugs --------
    // implementation("com.github.spotbugs:spotbugs-annotations:${spotbugs.toolVersion.get()}")
    spotbugs("com.github.spotbugs:spotbugs:${spotbugs.toolVersion.get()}")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

pmd {
    isIgnoreFailures = false
    toolVersion = "6.52.0"

    ruleSetFiles = files("$rootDir/buildSrc/config/pmd/rules.xml")
    ruleSets = emptyList()
}

spotbugs {
    ignoreFailures.set(false)
    toolVersion.set("4.7.3")
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
