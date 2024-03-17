plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":meta-reflect"))
    implementation(kotlin("reflect"))
}

application {
    mainClass = "zsu.meta.reflect.benchmark.MrBenchmarkKt"
}
