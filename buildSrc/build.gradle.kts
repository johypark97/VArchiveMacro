plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // https://plugins.gradle.org/plugin/org.openjfx.javafxplugin
    implementation(libs.javafx.plugin)

    // https://mvnrepository.com/artifact/org.bytedeco/gradle-javacpp
    implementation(libs.gradle.javacpp)

    // https://plugins.gradle.org/plugin/com.github.spotbugs
    implementation(libs.spotbugs.gradle.plugin)
}
