val buildBasename = "libjson"
val buildVersion = "1.0.0"

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
