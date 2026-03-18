plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "VArchiveMacro"

include("macro")

include("lib:common")
include("lib:desktop")
include("lib:hook")
include("lib:jfx")
include("lib:scanner")
