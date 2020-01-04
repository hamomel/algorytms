package ru.algo.search

import java.io.File

private val NOT_LETTERS_OR_DIGITS = Regex("([^\\p{Alnum}])")
private val DIGIT = Regex("\\p{Digit}")
private val ALL = Regex("\\p{all}")

/*
    calculates 16 bit hash
    currently it is CRC16 copied from the internet
 */
fun calculateHash(bytes: ByteArray): Int {
    var crc = 0xFFFF // initial value

    val polynomial = 0x1021 // 0001 0000 0010 0001  (0, 5, 12)

    for (b in bytes) {
        for (i in 0..7) {
            val bit = b.toInt() shr 7 - i and 1 == 1
            val c15 = crc shr 15 and 1 == 1
            crc = crc shl 1
            if (c15 xor bit) crc = crc xor polynomial
        }
    }

    crc = crc and 0xffff
    return crc
}

fun File.getFilesRecursively(files: MutableList<File>): List<File> {
    if (!this.isDirectory) {
        files.add(this)
    } else {
        this.listFiles()?.forEach { it.getFilesRecursively(files) }
    }
    return files
}

fun tokenize(text: String): List<String> =
    text.toLowerCase()
        .replace(NOT_LETTERS_OR_DIGITS, " ")
        .split(" ")
        .map {
            if (it.contains(DIGIT)) {
                it.replace(ALL, " ")
            } else {
                it
            }
        }
