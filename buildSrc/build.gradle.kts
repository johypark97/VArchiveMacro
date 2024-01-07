plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // https://plugins.gradle.org/plugin/com.github.spotbugs
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.13")

    // https://mvnrepository.com/artifact/org.bytedeco/gradle-javacpp
    implementation("org.bytedeco:gradle-javacpp:1.5.8")

    // https://plugins.gradle.org/plugin/org.openjfx.javafxplugin
    implementation("org.openjfx:javafx-plugin:0.1.0")
}
