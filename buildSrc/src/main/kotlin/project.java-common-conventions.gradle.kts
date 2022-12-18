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

    spotbugs("com.github.spotbugs:spotbugs:4.7.1")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

pmd {
    isIgnoreFailures = true
}

spotbugs {
    ignoreFailures.set(true)
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
