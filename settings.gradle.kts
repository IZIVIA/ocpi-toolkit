rootProject.name = "ocpi-toolkit"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(
    "annotation-processor",
    "transport",
    "ocpi-toolkit-2.2.1"
)
