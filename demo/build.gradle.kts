plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    ksp(project(":meta-reflect"))
    implementation(project(":demo:api"))
}
