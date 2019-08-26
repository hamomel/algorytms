import storage.IArray
import storage.SingleArray
import java.io.PrintStream
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
//    val date = Date().toString()
//    val outputFile = File(System.getProperty("user.dir"), "table-$date.txt")
//    val output = PrintStream(FileOutputStream(outputFile))
//
//        var count = 1000
//    while (count <= 1_000_000) {
//        val latch = CountDownLatch(7)
//
//        val table = Array<Array<String>>(7) {
//            Array<String>(5) { "" }
//        }
//
//        val arrays = arrayOf(
//            SingleArray<String>(),
//            VectorArray<String>(10),
//            FactorArray<String>(),
//            MatrixArray<String>(100),
//            LinkedArray<String>(),
//            SpacedArray<String>(),
//            NativeArray<String>()
//        )
//
//        for (i in 0 until arrays.size) {
//            thread {
//                val array = arrays[i]
//                println("calculate $count for ${array::class.java.simpleName}")
//                val row = table[i]
//                val time = measureTimeMillis {
//                    row[0] = array::class.java.simpleName
//                    row[1] = testAdd(array, count).toString()
//                    row[2] = testGet(array, count).toString()
//                    row[3] = testSet(array, count).toString()
//                    row[4] = testRemove(array, count).toString()
//                }
//
//                println("finished calculation $count for ${array::class.java.simpleName} in $time ms")
//                latch.countDown()
//            }
//        }
//
//        latch.await()
//        printResult(count.toString(), table, output)
//        count *= 10
//    }

    var arr = SingleArray<String>()
    testAdd(arr, 100000)

    println( "regular = " +
            measureTimeMillis {
                testSet(arr, 100000)
            }
    )

    arr = SingleArray<String>()
    testAdd(arr, 100000)

    println( "single = " +
            measureTimeMillis {
                for (i in 1..100000) {
                    arr.singleCopyAdd("$i", Random.nextInt(100000))
                }
            }
    )
}

private fun printResult(count: String, table: Array<Array<String>>, output: PrintStream) {
    val console = System.out
    System.setOut(output)

    val tests = arrayOf("add", "get", "midAdd", "remove")
    val maxLength = calculateMaxLength(table) + 1

    printLine((maxLength + 2) * 5)
    print("${addSpaces(count, maxLength)} |")
    for (test in tests) {
        print("${addSpaces(test, maxLength)} |")
    }
    println()

    for (row in table) {
        for (value in row) {
            print("${addSpaces(value, maxLength)} |")
        }
        println()
    }
    printLine((maxLength + 2) * 5)

    System.setOut(console)
}

fun calculateMaxLength(table: Array<Array<String>>): Int {
    var maxLength = 8
    for (row in table) {
        for (value in row) {
            if (value.length > maxLength) maxLength = value.length
        }
    }

    return maxLength
}

fun printLine(length: Int) {
    for (i in 0..length) print("—")
    println()
}

fun addSpaces(string: String, resLingth: Int): String =
    buildString {
        for (i in 1..(resLingth - string.length)) {
            append(" ")
        }
        append(string)
    }

fun testAdd(arr: IArray<String>, count: Int): Long {
    return measureTimeMillis {
        for (i in 0 until count) {
            arr.add("$i")
        }
    }
}

fun testGet(arr: IArray<String>, count: Int): Long {
    return measureTimeMillis {
        for (i in 0 until count) {
            arr.get(i)
        }
    }
}

fun testSet(arr: IArray<String>, count: Int): Long {
    return measureTimeMillis {
        for (i in 1 until count) {
            var random = Random.nextInt(arr.size() - 1)
            if (random == 0) random++
            arr.add("$i", random)
        }
    }
}

fun testRemove(arr: IArray<String>, count: Int): Long {
    return measureTimeMillis {
        for (i in 1 until count) {
            val random = Random.nextInt(arr.size() - 1)
            arr.remove(random)
        }
    }
}