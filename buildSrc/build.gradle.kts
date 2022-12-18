plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // https://plugins.gradle.org/plugin/com.github.spotbugs
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.13")
}
