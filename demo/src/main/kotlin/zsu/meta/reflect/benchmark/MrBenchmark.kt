package zsu.meta.reflect.benchmark

import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass

private val metaReflect = MReflect.get()

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

private class Stub

// us
data class Result(
    val preloadCost: Float,
    val firstTimeCost: Float,
    val avgCost: Float,
) {
    fun toCsv() = "$preloadCost,$firstTimeCost,$avgCost"
}

private inline fun benchMarkMain(count: Int, preload: () -> Unit, onEach: () -> Any): Result {
    var start = System.nanoTime()
    preload()
    val preloadCost = (System.nanoTime() - start) / 1000f
    println("preload: ${preloadCost}us")

    start = System.nanoTime()
    onEach()
    val firstTimeCost = (System.nanoTime() - start) / 1000f
    println("first: ${firstTimeCost}us")

    var allCost = 0f
    repeat(count) {
        start = System.nanoTime()
        val eachResult = onEach()
        val singleCost = (System.nanoTime() - start) / 1000f
        println("$it cost: ${singleCost}us, $eachResult")
        allCost += singleCost
    }

    return Result(preloadCost, firstTimeCost, allCost / count)
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
    }
    arg.output.appendText("${result.toCsv()}\n")
}

