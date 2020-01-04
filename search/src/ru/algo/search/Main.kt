package ru.algo.search

import java.io.File
import java.lang.IllegalArgumentException

const val MIN_WORD_LENGTH = 4
const val MAX_WORD_DISTANCE = 10

fun main(args: Array<String>) {
    val directory = args[0]

    val indexer = Indexer(directory)
    try {
        indexer.checkIndex()
    } catch (e: IllegalArgumentException) {
        println("Папка $directory не найдена")
        return
    }

    val files = indexer.files
    val index = indexer.index
    val searcher = Searcher(files, index)

    while (true) {
        val input = promptUser()
        if (input.toLowerCase() == "exit" || input.toLowerCase() == "выход") break
        val found = searcher.search(input)
        printResult(found, File(directory))
    }
}

private fun promptUser(): String {
    println("Введите строку для поиска:")
    return readLine()!!
}

private fun printResult(results:  List<SearchResult>, dir: File) {
    if (results.isEmpty()) {
        println("Строка не найдена")
        return
    }
    println("Искомая строка найдена в следующих документах:")
    results.forEach {
        println(it.file.toRelativeString(dir))
        it.phrases.forEach {
            println("позиция: ${it.key} ${it.value}")
        }
        println()
    }
}