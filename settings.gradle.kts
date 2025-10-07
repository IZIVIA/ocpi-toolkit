rootProject.name = "ocpi-toolkit"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(
    "annotation-processor",
    "transport",
    "ocpi-toolkit-2.2.1",
    "integrations:ocpi-toolkit-2.2.1-jackson",
    "integrations:ocpi-toolkit-2.2.1-kotlinx-serialization",
)
