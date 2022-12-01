val buildBasename = "libcommon"
val buildVersion = "1.0.0"

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
