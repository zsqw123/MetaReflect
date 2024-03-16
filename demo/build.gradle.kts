plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":meta-reflect"))
    implementation(project(":demo:api"))
    implementation(kotlin("reflect"))
}
