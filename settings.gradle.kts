plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "VArchiveMacro"

include("libcommon")
include("libdesktop")
include("libjfx")
include("libjfxhook")

include("macro")
