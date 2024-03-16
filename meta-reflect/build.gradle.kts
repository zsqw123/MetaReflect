plugins {
    kotlin("jvm")
    id("insidePublish")
}

dependencies {
    api(D.metadata)
}

tasks.test {
    useJUnitPlatform()
}
