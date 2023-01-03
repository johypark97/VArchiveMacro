val buildBasename = "libcommon"
val buildVersion = Version.makeVersionString()

plugins {
    id("project.java-library-conventions")
}

dependencies {
    api("com.github.kwhat:jnativehook:2.2.2")
    api("com.google.code.gson:gson:2.10")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
