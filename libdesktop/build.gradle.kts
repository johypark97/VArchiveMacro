version = findProperty("version") as String

plugins {
    id("buildlogic.java-library-conventions")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = version
    }

    archiveBaseName = "libdesktop"
    archiveVersion = version.toString()
}
