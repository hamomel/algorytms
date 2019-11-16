package main

import java.io.File
import kotlin.experimental.xor

fun main() {

}

fun encrypt(rawFile: String, encryptedFile: String, key: String) {
    val inFile = File(rawFile)
    val outFile = File(encryptedFile)
    xorFiles(inFile, outFile, key)
}

fun decrypt(encryptedFile: String, decryptedFile: String, key: String) {
    val inFile = File(encryptedFile)
    val outFile = File(decryptedFile)
    xorFiles(inFile, outFile, key)
    outFile.appendBytes("\n\n".toByteArray() + key.toByteArray())
}

private fun xorFiles(inFile: File, outFile: File, key: String) {
    inFile.inputStream().use { input ->
        val output = outFile.outputStream()
        val buffer = ByteArray(key.length * 16)
        val keyBytes = key.toByteArray()

        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            val enc = xorArray(buffer, keyBytes, read)
            output.write(enc)
        }
        output.flush()
        output.close()
    }
}

private fun xorArray(text: ByteArray, key: ByteArray, size: Int): ByteArray {
    val output = ByteArray(size)

    for (i in 0..size / key.size) {
        for (j in key.indices) {
            val index = i * key.size + j
            if (index == size) break
            output[index] = text[index] xor key[j]
        }
    }

    return output
}