package zsu.meta.reflect.benchmark

import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass
import kotlin.reflect.full.companionObject

private fun metaReflectNameBenchmark(): Int {
    val metaReflect = metaReflect
    metaReflect.mClass<SealedI>().sealedSubclassNames
    metaReflect.mClass<SealedC>().sealedSubclassNames
    metaReflect.mClass<SubTarget>().nestedClassNames
    metaReflect.mClass<CompanionTest>().companionObjectName
    metaReflect.mClass<SuperTest>().supertypes.size
    return 0
}

private fun kotlinReflectNameBenchmark(): Int {
    SealedI::class.sealedSubclasses
    SealedC::class.sealedSubclasses
    SubTarget::class.nestedClasses
    CompanionTest::class.companionObject
    SuperTest::class.supertypes.size
    return 0
}

object OnlyNamesBenchmark {
    fun run(arg: Args): Result {
        val result = when (arg.mode) {
            Args.Mode.KR_N -> benchMarkMain(arg.count, { Stub::class.supertypes }) {
                kotlinReflectionBenchmark()
            }


            Args.Mode.MR_N -> benchMarkMain(arg.count, { MReflect.preload() }) {
                metaReflectBenchmark()
            }

            else -> error("cannot process it")
        }
        return result
    }
}

fun main() {
    val args = arrayOf("-m", "KR_N", "-c", "10")
    val arg = Args(args)
    OnlyNamesBenchmark.run(arg)
}
