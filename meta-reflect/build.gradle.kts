plugins {
    kotlin("jvm")
    id("insidePublish")
}

dependencies {
    api(D.metadata)
    implementation(D.asm)
}

tasks.test {
    useJUnitPlatform()
}
