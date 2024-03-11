val buildBasename = "libhook"
val buildVersion = Version.makeVersionString()

plugins {
    id("project.java-library-conventions")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
