plugins {
    kotlin("jvm")
    id("insidePublish")
}

dependencies {
    implementation(D.ksp)
}

tasks.test {
    useJUnitPlatform()
}
