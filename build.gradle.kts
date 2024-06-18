plugins {
    id("inside") apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("host.bytedance.kotlin-cacheable") version "0.0.9-beta" apply false
}

subprojects {
    apply(plugin = "inside")
}
