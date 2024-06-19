plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:kotlinpoet:${Versions.kotlinPoet}")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoet}")
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}")
}
