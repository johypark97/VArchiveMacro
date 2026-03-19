plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "VArchiveMacro"

include("libcommon")
include("libdesktop")
include("libhook")
include("libjfx")

include("macro")
