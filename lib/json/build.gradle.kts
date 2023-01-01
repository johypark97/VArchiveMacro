val buildBasename = "libjson"
val buildVersion = Version.makeVersionString()

plugins {
    id("project.java-library-conventions")
}

dependencies {
    api("com.google.code.gson:gson:2.10")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
