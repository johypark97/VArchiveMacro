import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

val javacppPlatform = "windows-x86_64"

plugins {
    id("buildlogic.java-common-conventions")

    // gradle-javacpp-platform
    id("org.bytedeco.gradle-javacpp-platform")
}

dependencies {
    implementation(libs.tesseract.platform)
}
