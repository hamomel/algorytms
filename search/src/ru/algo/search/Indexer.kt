package ru.algo.search

import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.pow

private const val FILES_NAME = "files.tsv"
private const val INDEX_NAME = "index.tsv"
private const val DELIMITER = "\t"
private val SUPPORTED_FORMATS = arrayOf("txt", "json")

class Indexer(private val directory: String) {
    val index = arrayOfNulls<Array<Array<Int>>>(2.0.pow(16).toInt())
    var files = emptyArray<File>()
        private set

    private var wordsCount = 0
    private var collisionCount = 0
    private var filesCount = 0
    private var linesCount = 0

    fun checkIndex() {
        val dir = File(directory)
        if (!dir.exists() && !dir.isDirectory) {
            throw IllegalArgumentException("$directory does not exist or is a plain file")
        }
        files = getSupportedFiles(dir)
        val filesList = File(dir, FILES_NAME)
        val indexFile = File(dir, INDEX_NAME)

        if (!filesList.exists() || !indexFile.exists()) {
            println("Файлов индекса не найдено, начинаем полную индексацию")
            indexAll()
        } else {
            val savedFiles = getSavedFilesIfNotChanged(dir, filesList)
            if (savedFiles != null && files.size == savedFiles.size) {
                println("Файлы не изменились, восстанавливаем индекс с диска")
                fillIndex(indexFile, dir)
            } else {
                println("Файлы изменены, начинаем полную индексацию")
                indexAll()
            }
        }
    }

    private fun getSupportedFiles(dir: File) = dir.getFilesRecursively(mutableListOf())
        .filter { it.extension.toLowerCase() in SUPPORTED_FORMATS }
        .toTypedArray()

    private fun getSavedFilesIfNotChanged(dir: File, filesList: File): Array<File>? {
        return filesList.readLines()
            .map {
                val parts = it.split(DELIMITER)
                val file = File(dir, parts[0])
                val lastModified = parts[1].toLong()
                if (file.lastModified() != lastModified) return null
                return@map file
            }
            .toTypedArray()
    }

    private fun indexAll() {
        val hashToWords = mutableMapOf<Int, MutableSet<String>>()
        val uniqueWords = mutableSetOf<String>()
        files.forEachIndexed { filePosition, file ->
            var position = 0
            file.useLines { lines: Sequence<String> ->
                lines.forEach { line ->
                    val parts = tokenize(line)
                    parts.forEach { word ->
                        if (word.isNotBlank() && word.length >= MIN_WORD_LENGTH) {
                            uniqueWords.add(word)
                            val hash = calculateHash(word.toByteArray())
                            if (hashToWords[hash] == null) {
                                hashToWords[hash] = mutableSetOf(word)
                            } else if (hashToWords[hash]?.contains(word) != true) {
                                hashToWords[hash]?.add(word)
                                collisionCount++
                            }
                            val arrayForHash = index[hash]

                            if (arrayForHash != null) {
                                val i = arrayForHash.indexOfFirst { it[0] == filePosition }
                                if (i >= 0) {
                                    arrayForHash[i] = insertSorted(arrayForHash[i], position)
                                } else {
                                    index[hash] = Arrays.copyOf(arrayForHash, arrayForHash.size + 1)
                                    index[hash]?.set(arrayForHash.size, arrayOf(filePosition, position))
                                }
                            } else {
                                index[hash] = arrayOf(arrayOf(filePosition, position))
                            }
                        }

                        position += word.length + 1
                    }
                }
            }
            filesCount++
            print("Проиндексировано $filesCount файлов\r")
        }

        println("""
            Проиндексировано $filesCount файлов,
            найдено ${uniqueWords.size} слов,
            произошло $collisionCount коллизий
            """.trimIndent())
        println()

        saveIndex()
    }

    private fun insertSorted(indexArray: Array<Int>, position: Int): Array<Int> {
        val newArray = Arrays.copyOf(indexArray, indexArray.size + 1)
        var i = newArray.size - 1
        while (i > 0) {
            if (i == 1 || newArray[i - 1] < position) {
                newArray[i] = position
                break
            } else {
                newArray[i] = newArray[i - 1]
            }
            i--
        }

        return newArray
    }

    private fun saveIndex() {
        println("Сохраняем индекс на диск")
        val dir = File(directory)
        val filesFile = File(dir, FILES_NAME)
        val indexFile = File(dir, INDEX_NAME)

        thread {
            filesFile.bufferedWriter().use { writer ->
                files.forEach {
                    writer.write("${it.toRelativeString(dir)}$DELIMITER${it.lastModified()}")
                    writer.newLine()
                }
            }
        }

        thread {
            indexFile.bufferedWriter().use { writer ->
                index.forEachIndexed { index, arrayOfArrays ->
                    arrayOfArrays?.forEach {
                        val fileName = files[it[0]].toRelativeString(dir)
                        val indices = it.drop(1).joinToString(separator = DELIMITER)
                        writer.write("$index$DELIMITER$fileName$DELIMITER$indices")
                        writer.newLine()
                    }
                }
            }
        }
    }

    private fun fillIndex(indexFile: File, dir: File) {
        val filePositions = mutableMapOf<String, Int>()
        files.forEachIndexed { index, file ->
            filePositions[file.toRelativeString(dir)] = index
        }
        indexFile.useLines { lines: Sequence<String> ->
            var buffer = mutableListOf<Array<Int>>()
            var prevHash = -1

            lines.forEach { line ->
                val parts = line.split(DELIMITER)
                val hash = parts[0].toInt()
                val fileName = parts[1]
                val filePosition = filePositions[fileName]!!
                val indices = mutableListOf(filePosition)
                indices.addAll(parts.drop(2).map { it.toInt() })

                if (buffer.isEmpty() || hash == prevHash) {
                    buffer.add(indices.toTypedArray())
                    prevHash = hash
                } else {
                    index[prevHash] = buffer.toTypedArray()
                    buffer = mutableListOf(indices.toTypedArray())
                    prevHash = hash
                }

                linesCount++
                print("Считано $linesCount строк из файла индекса         \r")
            }

            if (prevHash >= 0 && buffer.isNotEmpty()) {
                index[prevHash] = buffer.toTypedArray()
            }
        }
        println()
        println("Индекс восстановлен")
    }
}