package zsu.meta.reflect.benchmark

import zsu.meta.reflect.*
import zsu.meta.reflect.demo.testMain


private val metaReflect = MReflect.get()

fun warmup() {
    Stub::class.supertypes
    MReflect.preload()
}

fun metaReflectBenchmark(): Int {
    metaReflect.mClass<SealedI>().sealedSubclasses
    metaReflect.mClass<SealedC>().sealedSubclasses
    return metaReflect.mClass<SubTarget>().nestedClasses // B
        .flatMap { (metaReflect.mClassFrom(it) as MClass).nestedClasses } // C
        .flatMap { (metaReflect.mClassFrom(it) as MClass).nestedClasses } // D
        .flatMap { (metaReflect.mClassFrom(it) as MClass).nestedClasses } // E
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

fun main() {
    testMain(1, { warmup() }) {
        metaReflectBenchmark()
    }

//    testMain(1, { warmup() }) {
//        kotlinReflectionBenchmark()
//    }
}

