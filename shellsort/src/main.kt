import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.io.PrintWriter
import java.lang.management.ManagementFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.random.Random

fun main() {
    testSorts("random") { createRandomArr(it) }
    testSorts("shuffled 10%") { createSortedArr(it).shuffle10percent() }
    testSorts("shuffled 5 elements") { createSortedArr(it).shuffle5Element() }
}

private fun testSorts(testName: String, arrayProvider: (Int) -> Array<Int>) {
    val table = mutableListOf<MutableList<String>>()

    var size: Int = 20
    var row = 0
    while (size <= 100_000) {
        table.add(MutableList(4) { "" })
        val arr = arrayProvider(size)
        table[row][0] = size.toString()

        val choose = chooseSort(arr.copyOf())
        val insert = insertionSort(arr.copyOf(), 0, 1)
        val shell = shellSort(arr.copyOf())

        table[row][1] = choose.get().toString()
        table[row][2] = insert.get().toString()
        table[row][3] = shell.get().toString()

        size *= 2
        row++
    }

    printResult(table, System.out)
    printToCsv(table, "table", testName)
}


fun measureCpuTime(block: () -> Unit): Future<Long> {
    val executor = Executors.newSingleThreadExecutor()
    val result = executor.submit<Long> {
        block()
        ManagementFactory.getThreadMXBean().currentThreadCpuTime / 1000
    }

    executor.shutdown()
    return result
}

fun chooseSort(arr: Array<Int>): Future<Long> = measureCpuTime {
    for (i in 0 until arr.size) {
        var lowest = i
        for (j in i + 1 until arr.size) {
            if (arr[j] < arr[lowest]) lowest = j
        }
        if (lowest != arr[i]) swap(arr, lowest, i)
    }
}

fun insertionSort(arr: Array<Int>, start: Int, step: Int): Future<Long> = measureCpuTime {
    for (i in start + step until arr.size step step) {
        if (arr[i] >= arr[i - step]) continue

        for (j in i downTo step step step) {
            if (arr[j] < arr[j - step]) {
                swap(arr, j, j - step)
            }
        }
    }
//    printArray(arr)
}

fun shellSort(arr: Array<Int>): Future<Long> = measureCpuTime {
    var d = arr.size / 2

    while (d > 0) {
        for (i in 0 until d) {
            insertionSort(arr, i, d)
        }

        d /= 2
    }
//    printArray(arr)
}

fun <T> swap(arr: Array<T>, first: Int, second: Int) {
    val temp = arr[second]
    arr[second] = arr[first]
    arr[first] = temp
}

fun createRandomArr(volume: Int): Array<Int> = Array(volume) { Random.nextInt(100) }
fun createSortedArr(volume: Int): Array<Int> = Array(volume) { i -> i }

fun <T> Array<T>.shuffle10percent(): Array<T> {
    for (i in 0 until size / 10) {
        swap(this, i, Random.nextInt(this.size - 1))
    }

    return this
}

fun <T> Array<T>.shuffle5Element(): Array<T> {
    for (i in 0 until 5 step size / 5) {
        swap(this, i, Random.nextInt(this.size - 1))
    }

    return this
}

fun printArray(arr: Array<Int>) {
    arr.forEachIndexed { index, i ->
        if (index % 50 == 0) println()
        print("$i ")
    }
    println()
}

private fun printResult(table: List<List<String>>, output: PrintStream) {
    val console = System.out
    System.setOut(output)

    val algorithms = arrayOf("", "choose", "insert", "shell")
    val maxLength = calculateMaxLength(table) + 1

    printLine((maxLength + 2) * 4)

    for (test in algorithms) {
        print("${addSpaces(test, maxLength)} |")
    }
    println()

    for (row in table) {
        for (value in row) {
            print("${addSpaces(value.toString(), maxLength)} |")
        }
        println()
    }
    printLine((maxLength + 2) * 4)

    System.setOut(console)
}

fun calculateMaxLength(table: List<List<String>>): Int {
    var maxLength = 8
    for (row in table) {
        for (value in row) {
            if (value.toString().length > maxLength) maxLength = value.toString().length
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

fun printToCsv(table: List<List<String>>, fileName: String, testName: String) {
    val arr = mutableListOf(listOf("arr size", "choose", "insert", "shell")) + table

    val date = Date().time.toString()
    val outputFile = FileWriter(File(System.getProperty("user.dir"), "$fileName.csv"), true)
    PrintWriter(outputFile).use { writer ->
        writer.println()
        writer.println(testName)
        arr.forEach {
            writer.println(
                it.joinToString(",")
            )
        }
    }
}