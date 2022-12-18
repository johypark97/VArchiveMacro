plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    // implementation("com.google.guava:guava:31.0.1-jre")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
