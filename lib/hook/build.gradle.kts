val buildVersion = Version.makeVersionString()

plugins {
    id("buildlogic.java-library-conventions")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName = "libhook"
    archiveVersion = buildVersion
}
