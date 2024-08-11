val buildVersion = Version.makeVersionString()

plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    implementation(project(":lib:common"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = buildVersion
    }

    archiveBaseName = "libscanner"
    archiveVersion = buildVersion
}
