import storage.IArray
import storage.SingleArray
import storage.VectorArray
import kotlin.system.measureTimeMillis

fun main() {
    val simpleArray = SingleArray<String>()
    val vectorArray = VectorArray<String>(10)

    testAdd(simpleArray, 100_000)
    testAdd(vectorArray, 100_000)

    testGet(simpleArray, 100_000)
    testGet(vectorArray, 100_000)

    testSet(simpleArray, 100_000)
    testSet(vectorArray, 100_000)

    testRemove(simpleArray, 100_000)
    testRemove(vectorArray, 100_000)
}

fun printArray(arr: IArray<String>) {
    print("[ ")
    for (i in 0 until arr.size()) {
        print(" ${arr.get(i)}")
    }
    println(" ]")
}

fun testAdd(arr: IArray<String>, count: Int) {
    val time = measureTimeMillis {
        for (i in 0 until count) {
            arr.add("$i")
        }
    }

    println("add ${arr::class.java.simpleName} - $time")
}

fun testGet(arr: IArray<String>, count: Int) {
    val time = measureTimeMillis {
        for (i in 0 until count) {
            arr.get(i)
        }
    }

    println("get ${arr::class.java.simpleName} - $time")
}

fun testSet(arr: IArray<String>, count: Int) {
    val time = measureTimeMillis {
        for (i in 0 until count) {
            arr.add("$i", i)
        }
    }

    println("set ${arr::class.java.simpleName} - $time")
}

fun testRemove(arr: IArray<String>, count: Int) {
    val time = measureTimeMillis {
        for (i in count downTo 0) {
            arr.remove(i)
        }
    }

    println("remove ${arr::class.java.simpleName} - $time")
}