package test

import main.decrypt
import main.encrypt
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class Test {
    private val testDir = System.getProperty("user.dir") + "/testfiles"
    private val originalFile = "$testDir/original.txt"
    private val encryptedFile = "$testDir/encrypted.txt"
    private val decryptedFile = "$testDir/decrypted.txt"
    private val key ="Разработка программы, реализующей идею XOR шифрования"


    @Test
    fun testEncryptDecrypt() {
        encrypt(originalFile, encryptedFile, key)
        decrypt(encryptedFile, decryptedFile, key)

        val originalText = File(originalFile).readLines()
        val decryptedLines = File(decryptedFile).readLines()
        val decryptedText = decryptedLines.take(decryptedLines.size - 2)
        val decryptedKey = decryptedLines.last()

        assertEquals(originalText, decryptedText)
        assertEquals(key, decryptedKey)
    }
}