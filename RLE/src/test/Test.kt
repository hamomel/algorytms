package test

import main.main
import org.junit.Test
import java.io.File
import kotlin.random.Random
import kotlin.test.assertEquals

class Test {

    private val baseDir = System.getProperty("user.dir") + "/testfiles"
    private val inputFile = "$baseDir/lorem.txt"
    private val outputFile = "$baseDir/lorem.cmp"
    private val decompressed = "$baseDir/lorem.dcmp.txt"

    @Test
    fun testCompressDecompress() {
        main(arrayOf("-compress", inputFile))
        main(arrayOf("-decompress", outputFile, "-o", decompressed))

        val initial = File(inputFile).readText()
        val final = File(decompressed).readText()

        println("testRLE: ${File(inputFile).length()} vs ${File(outputFile).length()}")
        assertEquals(initial, final)
    }

    @Test
    fun testCompressDecompressImproved() {
        main(arrayOf("-compress-improved", inputFile))
        main(arrayOf("-decompress-improved", outputFile, "-o", decompressed))

        val initial = File(inputFile).readText()
        val final = File(decompressed).readText()

        println("testImproved: ${File(inputFile).length()} vs ${File(outputFile).length()}")
        assertEquals(initial, final)
    }

    @Test
    fun testImprovedOnRepeating() {
        val inFile = File("$baseDir/filled.txt")
        val cmpFile = File("$baseDir/filled.cmp")
        val dcmpFile = File("$baseDir/filled.dcmp.txt")

        inFile.outputStream().use { stream ->
            val buffer = ByteArray(1000)
            for (i in 1..10000) {
                buffer[i % 1000] =
                    if ((i / 1000) % 2 == 0) {
                        'a'.toByte()
                    } else {
                        Random.nextInt(32, 126).toByte()
                    }

                if (i % 1000 == 0) stream.write(buffer)
            }
        }

        main(arrayOf("-compress-improved", inFile.name, "-o", cmpFile.name))
        main(arrayOf("-decompress-improved", cmpFile.name, "-o", dcmpFile.name))

        val initial = inFile.readText()
        val final = dcmpFile.readText()

        println("testImprovedRepeating: ${inFile.length()} vs ${cmpFile.length()}")
        assertEquals(initial, final)
    }
}