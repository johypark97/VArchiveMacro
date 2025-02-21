version = findProperty("version") as String

plugins {
    id("buildlogic.java-javacpp-conventions")
    id("buildlogic.java-library-conventions")
}

dependencies {
    implementation(project(":lib:common"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = version
    }

    archiveBaseName = "libscanner"
    archiveVersion = version.toString()
}
