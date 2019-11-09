package main

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

private const val COMMAND_COMPRESS = "-compress"
private const val COMMAND_COMPRESS_IMPROVED = "-compress-improved"
private const val COMMAND_DECOMPRESS = "-decompress"
private const val COMMAND_DECOMPRESS_IMPROVED = "-decompress-improved"
private const val OPTION_OUTPUT = "-o"

class Stub

fun main(args: Array<String>) {
    if (args.size < 2) {
        showHelp()
        return
    }

    when (args[0]) {
        COMMAND_COMPRESS, COMMAND_COMPRESS_IMPROVED -> {
            val outFile = if (args.size == 4 && args[2] == OPTION_OUTPUT) {
                args[3]
            } else {
                args[1].substringBeforeLast(".") + ".cmp"
            }

            compressFile(args[1], outFile, args[0] == COMMAND_COMPRESS_IMPROVED)
        }
        COMMAND_DECOMPRESS, COMMAND_DECOMPRESS_IMPROVED -> {
            val outFile = if (args.size == 4 && args[2] == OPTION_OUTPUT) {
                args[3]
            } else {
                args[1].substringBeforeLast(".") + ".dcmp"
            }
            if (args[0] == COMMAND_DECOMPRESS) {
                decompressFile(args[1], outFile)
            } else {
                decompressFileImproved(args[1], outFile)
            }
        }
        else -> showHelp()
    }
}

private fun showHelp() {
    val input = Stub::class.java.getResourceAsStream("help.txt")
    val text = BufferedReader(InputStreamReader(input)).use { it.readText() }
    println(text)
}

private fun compressFile(name: String, outputName: String, useImproved: Boolean) {
    val inputFile = openFile(name)
    val outputFile = openFile(outputName)

    inputFile.inputStream().use { input ->
        val output = outputFile.outputStream()
        val buffer = ByteArray(1024)
        while (input.available() > 0) {
            val read = input.read(buffer)
            val compressed = if (useImproved) {
                compressArrayImproved(buffer, read)
            } else {
                compressArray(buffer, read)
            }
            output.write(compressed)
        }
        output.close()
    }
}

private fun decompressFile(name: String, outputName: String) {
    val inputFile = openFile(name)
    val outputFile = openFile(outputName)

    inputFile.inputStream().use { input ->
        val output = outputFile.outputStream()
        val buffer = ByteArray(1024)
        while (input.available() > 0) {
            val read = input.read(buffer)
            val compressed = decompressArray(buffer, read)
            output.write(compressed)
        }
        output.close()
    }
}

private fun openFile(name: String): File {
    val fullName = if (name.startsWith("~")) {
        System.getProperty("user.home") + name.substringAfter("~")
    } else {
        name
    }
    return File(fullName)
}

// We use explicit size because when read from file array might be full
private fun compressArray(arr: ByteArray, size: Int): ByteArray {
    var result = ByteArray(size)
    var count = 1
    var added = 0

    for (index in 0 until size) {
        if (index < size - 1 && count < 127 && arr[index] == arr[index + 1]) {
            count++
        } else {
            if (added + count >= result.size) {
                result = result.copyOf(result.size * 2)
            }
            result[added] = count.toByte()
            result[added + 1] = arr[index]
            added += 2
            count = 1
        }
    }

    return result.copyOf(added)
}

// We use explicit size because when read from file array might be full
private fun decompressArray(arr: ByteArray, size: Int): ByteArray {
    var result = ByteArray(size * 2)
    var readPosition = 0
    var writePosition = 0

    while (readPosition < size) {
        val count = arr[readPosition].toInt()
        if (writePosition + count >= result.size) {
            result = result.copyOf(result.size * 2)
        }
        for (i in 0 until count) {
            result[writePosition + i] = arr[readPosition + 1]
        }
        writePosition += count
        readPosition += 2
    }

    return result.copyOf(writePosition)
}

private fun compressArrayImproved(arr: ByteArray, size: Int): ByteArray {
    var result = ByteArray(size)
    var count = 1
    var added = 0

    fun writeRepeating(lastPosition: Int) {
        if (added + 2 > result.size) result = result.copyOf(result.size * 2)
        result[added] = count.toByte()
        result[added + 1] = arr[lastPosition]
        added += 2
    }

    fun writeNotRepeating(lastPosition: Int) {
        val positiveCount = -count
        if (added + positiveCount + 1 > result.size) result = result.copyOf(result.size * 2)
        result[added] = count.toByte()
        System.arraycopy(arr, lastPosition - (positiveCount - 1), result, added + 1, positiveCount)
        added += (positiveCount + 1)
    }

    for (i in 0 until size - 1) {
        if (arr[i] == arr[i + 1]) {
            when {
                count in 1..126 -> count++
                count > 126 -> {
                    writeRepeating(i)
                    count = 1
                }
                count < 0 -> {
                    count++
                    writeNotRepeating(i - 1)
                    count = 2
                }
            }
        } else {
            when {
                count == 1 -> count = -2
                count in -126..-1 -> count--
                count < -126 -> {
                    writeNotRepeating(i)
                    count = 1
                }
                count > 0 -> {
                    writeRepeating(i)
                    count = 1
                }
            }
        }

        if (i == size - 2) {
            if (arr[i] == arr[i + 1]) {
                writeRepeating(i + 1)
            } else {
                if (count == 1) count = -1
                writeNotRepeating(i + 1)
            }
        }
    }

    return result.copyOf(added)
}

fun decompressFileImproved(name: String, outputName: String) {
    val inputFile = openFile(name).inputStream()
    val outputFile = openFile(outputName).outputStream()
    val buffer = ByteArray(128)

    while (inputFile.available() > 0) {
        inputFile.read(buffer, 0, 1)
        val count = buffer[0].toInt()

        if (count > 0) {
            inputFile.read(buffer, 0, 1)
            for (i in 1 until count) {
                buffer[i] = buffer[0]
            }
            outputFile.write(buffer, 0, count)
        } else {
            val positiveCount = -count
            inputFile.read(buffer, 0, positiveCount)
            outputFile.write(buffer, 0, positiveCount)
        }
    }
}