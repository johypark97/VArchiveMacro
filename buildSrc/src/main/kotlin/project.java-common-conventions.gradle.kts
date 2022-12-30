import com.github.spotbugs.snom.SpotBugsTask

plugins {
    java

    // Static analysis tools
    pmd
    id("com.github.spotbugs")
}

repositories {
    mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    // implementation("com.google.guava:guava:31.0.1-jre")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    // Spotbugs
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
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.withType<SpotBugsTask> {
    reports.create("html") {
        required.set(true)
        setStylesheet("fancy-hist.xsl")
    }
}
