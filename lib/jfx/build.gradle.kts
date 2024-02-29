val buildBasename = "libjfx"
val buildVersion = Version.makeVersionString()

plugins {
    id("project.java-library-conventions")
}

dependencies {
    api(project(":lib:common"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
