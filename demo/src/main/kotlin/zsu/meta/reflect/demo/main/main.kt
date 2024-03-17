package zsu.meta.reflect.demo.main

import java.io.File

fun main(args: Array<String>) {
    val path = args[0].trim()
    val jar = File(path, "demo-all.jar")
    val output = File(path, "outputMN.csv")
    repeat(100) {
        if (it % 10 == 0) {
            println("progress: $it")
        }
        val process = ProcessBuilder()
            .directory(File(path).absoluteFile)
            .command(
                "java", "-jar", jar.absolutePath,
                "-m", "MR_N",
                "-c", "30",
                "-o", output.absolutePath,
            )
        process.start().waitFor()
    }
}



