plugins {
    id("inside") apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

subprojects {
    apply(plugin = "inside")
}
