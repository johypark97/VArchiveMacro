val buildBasename = "libscanner"
val buildVersion = Version.makeVersionString()

plugins {
    id("project.java-library-conventions")
}

dependencies {
    api(project(":lib:common"))

    api("org.bytedeco:tesseract-platform:5.2.0-1.5.8")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName.set(buildBasename)
    archiveVersion.set(buildVersion)
}
