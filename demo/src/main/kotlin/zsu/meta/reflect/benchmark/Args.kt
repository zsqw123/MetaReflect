package zsu.meta.reflect.benchmark

import java.io.File

class Args(args: Array<String>) {
    var output: File = File("output.csv").absoluteFile
        private set
    var mode: Mode = Mode.MR
        private set

    var count: Int = 5
        private set

    init {
        for ((index, s) in args.withIndex()) {
            fun next() = args[index + 1].trim()
            when (s.trim()) {
                "-m" -> mode = Mode.valueOf(next())
                "-o" -> output = File(next())
                "-c" -> count = next().toInt()
            }
        }
    }

    enum class Mode { KR, MR }
}

