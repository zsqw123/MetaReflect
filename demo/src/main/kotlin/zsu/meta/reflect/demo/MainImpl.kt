package zsu.meta.reflect.demo

import zsu.meta.reflect.JClassName
import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass

sealed interface SealedParent

class ChildA(private val a: String) : SealedParent

class ChildB(private val b: StringBuilder) : SealedParent

fun reflectTest(metaReflect: MReflect): List<JClassName> {
    val sealedParentClass = metaReflect.mClass<SealedParent>()
    return sealedParentClass.sealedSubclasses
}

fun main() {
    var start = System.nanoTime()
    val metaReflect = MReflect.get()
    MReflect.preload()
    println("preload: ${(System.nanoTime() - start) / 1000_000f}ms")

    start = System.nanoTime()
    val firstTime = reflectTest(metaReflect)
    println(firstTime)
    println("cost: ${(System.nanoTime() - start) / 1000_000f}ms")

    repeat(3) {
        start = System.nanoTime()
        val secondTime = reflectTest(metaReflect)
        println(secondTime)
        println("$it cost: ${(System.nanoTime() - start) / 1000_000f}ms")
    }
}
