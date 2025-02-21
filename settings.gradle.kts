plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "VArchiveMacro"

include("dbmanager")
include("macro")

include("lib:common")
include("lib:desktop")
include("lib:hook")
include("lib:jfx")
include("lib:scanner")
