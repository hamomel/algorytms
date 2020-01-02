package ru.algo.search

import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.pow

private const val FILES_NAME = "files.tsv"
private const val INDEX_NAME = "index.tsv"
private const val DELIMITER = "\t"
private val SUPPORTED_FORMATS = arrayOf("txt")

class Indexer(private val directory: String) {
    val index = arrayOfNulls<Array<Array<Int>>>(2.0.pow(16).toInt())
    var files = emptyArray<File>()
        private set

    private var wordsCount = 0
    private var collisionCount = 0

    fun checkIndex() {
        val dir = File(directory)
        if (!dir.exists() && !dir.isDirectory) {
            throw IllegalArgumentException("$directory does not exist or is a plain file")
        }
        files = getSupportedFiles(dir)
        val filesList = File(dir, FILES_NAME)
        val indexFile = File(dir, INDEX_NAME)

        if (!filesList.exists() || !indexFile.exists()) {
            println("files don't exist, start full index")
            indexAll()
        } else {
            val savedFiles = getSavedFilesIfNotChanged(dir, filesList)
            if (savedFiles != null && files.size == savedFiles.size) {
                println("files up to date, restoring index from disk")
                fillIndex(indexFile, dir)
            } else {
                println("files are changed, start full index")
                indexAll()
            }
        }
    }

    private fun getSupportedFiles(dir: File) = dir.getFilesRecursively(mutableListOf())
        .filter { it.extension in SUPPORTED_FORMATS }
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
        // TODO remove next line after removing logs
        val dir = File(directory)
        files.forEachIndexed { filePosition, file ->
            // TODO remove next line after removing logs
            val fileName = file.toRelativeString(dir)
            println("indexing file $fileName")

            var position = 0
            file.useLines { lines: Sequence<String> ->
                lines.forEach { line ->
                    val parts = tokenize(line)
                    parts.forEach { word ->
                        if (word.length >= MIN_WORD_LENGTH) {
                            val hash = calculateHash(word.toByteArray())
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
                                wordsCount++
                                index[hash] = arrayOf(arrayOf(filePosition, position))
                            }
                        }

                        position += word.length + 1
                    }
//                    println("indexed position: $position in $fileName")
                }
            }
        }

        println("""
            Проиндексировано ${files.size} файлов,
            найдено $wordsCount слов,
            произошло $collisionCount коллизий
            """.trimIndent())
        println()
        saveIndex()
    }

    private fun insertSorted(indexArray: Array<Int>, position: Int): Array<Int> {
        var collisionReported = false
        val newArray = Arrays.copyOf(indexArray, indexArray.size + 1)
        var i = newArray.size - 1
        while (i > 0) {
            if (i == 1 || newArray[i - 1] < position) {
                newArray[i] = position
                break
            } else {
                newArray[i] = newArray[i - 1]
                if (!collisionReported) collisionCount++
                collisionReported = true
            }
            i--
        }

        return newArray
    }

    private fun saveIndex() {
        println("saving")
        val dir = File(directory)
        val filesFile = File(dir, FILES_NAME)
        val indexFile = File(dir, INDEX_NAME)

        thread {
            println("saving files")
            filesFile.bufferedWriter().use { writer ->
                files.forEach {
                    writer.write("${it.toRelativeString(dir)}$DELIMITER${it.lastModified()}")
                    writer.newLine()
                }
            }
        }

        thread {
            println("saving indices")
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
        indexFile.useLines { lines: Sequence<String> ->
            var buffer = mutableListOf<Array<Int>>()
            var prevHash = -1

            lines.forEach { line ->
                val parts = line.split(DELIMITER)
                val hash = parts[0].toInt()
                val fileName = parts[1]
                val filePosition = files.indexOfFirst { it.toRelativeString(dir) == fileName }
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
            }

            if (prevHash >= 0 && buffer.isNotEmpty()) {
                index[prevHash] = buffer.toTypedArray()
            }
        }
        println("index resored")
    }
}