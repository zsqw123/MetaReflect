plugins {
    kotlin("jvm")
    id("insidePublish")
    id("host.bytedance.kotlin-cacheable")
}

dependencies {
    api(D.metadata)
    implementation(D.asm)
}

tasks.test {
    useJUnitPlatform()
}
