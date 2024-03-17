package zsu.meta.reflect.benchmark

inline fun benchMarkMain(count: Int, preload: () -> Unit, onEach: () -> Any): Result {
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
