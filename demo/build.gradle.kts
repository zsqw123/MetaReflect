plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":meta-reflect"))
    implementation(project(":demo:api"))
    implementation(kotlin("reflect"))
}
