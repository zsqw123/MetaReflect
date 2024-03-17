package zsu.meta.reflect.benchmark

import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass

val metaReflect = MReflect.get()

fun metaReflectBenchmark(): Int {
    val metaReflect = metaReflect
    metaReflect.mClass<SealedI>().sealedSubclasses
    metaReflect.mClass<SealedC>().sealedSubclasses
    return metaReflect.mClass<SubTarget>().nestedClasses // B
        .flatMap { it.nestedClasses } // C
        .flatMap { it.nestedClasses } // D
        .flatMap { it.nestedClasses } // E
        .size
}

fun kotlinReflectionBenchmark(): Int {
    SealedI::class.sealedSubclasses
    SealedC::class.sealedSubclasses
    return SubTarget::class.nestedClasses //B
        .flatMap { it.nestedClasses } // C
        .flatMap { it.nestedClasses } // D
        .flatMap { it.nestedClasses } // E
        .size
}

class Stub

// us
data class Result(
    val preloadCost: Float,
    val firstTimeCost: Float,
    val avgCost: Float,
) {
    fun toCsv() = "$preloadCost,$firstTimeCost,$avgCost"
}

fun main(args: Array<String>) {
    val arg = Args(args)
    val result = when (arg.mode) {
        Args.Mode.KR -> benchMarkMain(arg.count, { Stub::class.supertypes }) {
            kotlinReflectionBenchmark()
        }


        Args.Mode.MR -> benchMarkMain(arg.count, { MReflect.preload() }) {
            metaReflectBenchmark()
        }

        Args.Mode.KR_N, Args.Mode.MR_N -> OnlyNamesBenchmark.run(arg)
    }
    arg.output.appendText("${result.toCsv()}\n")
}

//fun main() {
//    benchMarkMain(10, { Stub::class.supertypes }) {
//        kotlinReflectionBenchmark()
//    }
//    benchMarkMain(10, { MReflect.preload() }) {
//        metaReflectBenchmark()
//    }
//}

