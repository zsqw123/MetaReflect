package zsu.meta.reflect.demo

import zsu.meta.reflect.MReflect
import zsu.meta.reflect.mClass

sealed interface SealedParent

class ChildA(private val a: String) : SealedParent
class ChildB(private val b: StringBuilder) : SealedParent

inline fun testMain(preload: () -> Unit, onEach: (Int) -> Any) {
    var start = System.nanoTime()
    preload()
    println("preload: ${(System.nanoTime() - start) / 1000_000f}ms")
    repeat(4) {
        start = System.nanoTime()
        println(onEach(it))
        println("$it cost: ${(System.nanoTime() - start) / 1000_000f}ms")
    }
}

fun ktMain() = testMain(preload = { Stub::class.supertypes }) {
    SealedParent::class.sealedSubclasses
}

fun mMain() = testMain(preload = { MReflect.preload() }) {
    MReflect.get().mClass<SealedParent>().sealedSubclasses
}

fun main() {
    mMain()
//    ktMain()
}

private class Stub
