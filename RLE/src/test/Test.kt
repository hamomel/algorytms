package test

import main.main
import org.junit.Before
import org.junit.Test
import java.io.File
import java.lang.StringBuilder
import kotlin.random.Random
import kotlin.test.assertEquals

class Test {

    private val baseDir = System.getProperty("user.dir") + "/testfiles"
    private val outDir = System.getProperty("user.dir") + "/testfiles/out"
    private val inputFile = "$baseDir/text.txt"
    private val outputFile = "$outDir/text.cmp"
    private val decompressed = "$outDir/text.txt"

    @Before
    fun setup() {
        val outFile = File(outDir)
        if (!outFile.exists()) {
            outFile.mkdirs()
        }
        outFile.listFiles()?.forEach {
            it.delete()
        }
    }

    @Test
    fun testCompressDecompress() {
        main(arrayOf("-compress", inputFile, "-o", outputFile))
        main(arrayOf("-decompress", outputFile, "-o", decompressed))

        val initial = File(inputFile).readText()
        val final = File(decompressed).readText()

        assertEquals(initial, final)
    }

    @Test
    fun testCompressDecompressImproved() {
        main(arrayOf("-compress-improved", inputFile, "-o", outputFile))
        main(arrayOf("-decompress-improved", outputFile, "-o", decompressed))

        val initial = File(inputFile).readText()
        val final = File(decompressed).readText()

        assertEquals(initial, final)
    }

    @Test
    fun testImprovedOnRepeating() {
        val inFile = File("$baseDir/repeatingText.txt")
        val cmpFile = File("$outDir/repeatingText.cmp")
        val dcmpFile = File("$outDir/repeatingText.dcmp.txt")

        main(arrayOf("-compress-improved", inFile.absolutePath, "-o", cmpFile.absolutePath))
        main(arrayOf("-decompress-improved", cmpFile.absolutePath, "-o", dcmpFile.absolutePath))

        val initial = inFile.readText()
        val final = dcmpFile.readText()

        assertEquals(initial, final)
    }

    @Test
    fun testRawImage() {
        val inFile = File("$baseDir/rawImage.dng")
        val cmpFile = File("$outDir/rawImage.cmp")
        val dcmpFile = File("$outDir/rawImage.dng")

        main(arrayOf("-compress-improved", inFile.absolutePath, "-o", cmpFile.absolutePath))
        main(arrayOf("-decompress-improved", cmpFile.absolutePath, "-o", dcmpFile.absolutePath))

        val initial = inFile.readText()
        val final = dcmpFile.readText()

        assertEquals(initial, final)
    }

    @Test
    fun testOnDifferentFiles() {
        val reportFile = File(System.getProperty("user.dir") + "/report.csv")
        val textFile = File("$baseDir/text.txt")
        val cmpTextFile = File("$outDir/text.cmp")
        val repeatingTextFile = File("$baseDir/repeatingText.txt")
        val cmpRepeatingTextFile = File("$outDir/repeatingText.cmp")
        val audioFile = File("$baseDir/sound.wav")
        val cmpAudioFile = File("$outDir/sound.cmp")
        val rawImageFile = File("$baseDir/rawImage.dng")
        val cmpRawImageFile = File("$outDir/rawImage.cmp")
        val imageFile = File("$baseDir/image.jpg")
        val cmpImageFile = File("$outDir/image.cmp")

        val reportWriter = reportFile.bufferedWriter()
        reportWriter.write("file type,initial size,RLE compressed,percentage,improved compressed,percentage")
        reportWriter.newLine()

        main(arrayOf("-compress", textFile.absolutePath, "-o", cmpTextFile.absolutePath))
        reportWriter.write("text,${textFile.length()},${cmpTextFile.length()},${cmpTextFile.length().toDouble() / textFile.length() * 100},")

        main(arrayOf("-compress-improved", textFile.absolutePath, "-o", cmpTextFile.absolutePath))
        reportWriter.append("${cmpTextFile.length()},${cmpTextFile.length().toDouble() / textFile.length() * 100}")
        reportWriter.newLine()

        main(arrayOf("-compress", repeatingTextFile.absolutePath, "-o", cmpRepeatingTextFile.absolutePath))
        reportWriter.write("repeating text,${repeatingTextFile.length()},${cmpRepeatingTextFile.length()},${cmpRepeatingTextFile.length().toDouble() / repeatingTextFile.length() * 100},")

        main(arrayOf("-compress-improved", repeatingTextFile.absolutePath, "-o", cmpRepeatingTextFile.absolutePath))
        reportWriter.append("${cmpRepeatingTextFile.length()},${cmpRepeatingTextFile.length().toDouble() / repeatingTextFile.length() * 100}")
        reportWriter.newLine()

        main(arrayOf("-compress", audioFile.absolutePath, "-o", cmpAudioFile.absolutePath))
        reportWriter.write("audio,${audioFile.length()},${cmpAudioFile.length()},${cmpAudioFile.length().toDouble() / audioFile.length() * 100},")

        main(arrayOf("-compress-improved", audioFile.absolutePath, "-o", cmpAudioFile.absolutePath))
        reportWriter.append("${cmpAudioFile.length()},${cmpAudioFile.length().toDouble() / audioFile.length() * 100}")
        reportWriter.newLine()

        main(arrayOf("-compress", imageFile.absolutePath, "-o", cmpImageFile.absolutePath))
        reportWriter.write("image,${imageFile.length()},${cmpImageFile.length()},${cmpImageFile.length().toDouble() / imageFile.length() * 100},")

        main(arrayOf("-compress-improved", imageFile.absolutePath, "-o", cmpImageFile.absolutePath))
        reportWriter.append("${cmpImageFile.length()},${cmpImageFile.length().toDouble() / imageFile.length() * 100}")
        reportWriter.newLine()

        main(arrayOf("-compress", rawImageFile.absolutePath, "-o", cmpRawImageFile.absolutePath))
        reportWriter.write("raw image,${rawImageFile.length()},${cmpRawImageFile.length()},${cmpRawImageFile.length().toDouble() / rawImageFile.length() * 100},")

        main(arrayOf("-compress-improved", rawImageFile.absolutePath, "-o", cmpRawImageFile.absolutePath))
        reportWriter.append("${cmpRawImageFile.length()},${cmpRawImageFile.length().toDouble() / rawImageFile.length() * 100}")
        reportWriter.newLine()

        reportWriter.flush()
        reportWriter.close()
    }

    fun String.addSpacesTo(length: Int): String {
        val builder = StringBuilder(this)
        while (builder.length < length) {
            builder.append(' ')
        }

        return builder.toString()
    }

    private fun createRepeatingTextFile(file: File) {
        file.outputStream().use { stream ->
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
    }
}