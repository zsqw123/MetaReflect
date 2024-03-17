package zsu.meta.reflect.demo

import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass

sealed interface SealedParent

class ChildA(private val a: String) : SealedParent
class ChildB(private val b: StringBuilder) : SealedParent

inline fun testMain(count: Int = 4, preload: () -> Unit, onEach: (Int) -> Any) {
    var start = System.nanoTime()
    preload()
    println("preload: ${(System.nanoTime() - start) / 1000f}us")
    repeat(count) {
        start = System.nanoTime()
        println(onEach(it))
        println("$it cost: ${(System.nanoTime() - start) / 1000f}us")
    }
}

fun ktMain() = testMain(preload = { Stub::class.supertypes }) {
    SealedParent::class.sealedSubclasses
}

fun mMain() = testMain(preload = { MReflect.preload() }) {
    MReflect.get().mClass<SealedParent>().sealedSubclassNames
}

fun main() {
    mMain()
//    ktMain()
}

private class Stub
