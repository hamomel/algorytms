package ru.algo.search

import java.lang.IllegalArgumentException

const val MIN_WORD_LENGTH = 3
const val MAX_WORD_DISTANCE = 14

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
        printResult(found)
    }
}

private fun promptUser(): String {
    println("Введите строку для поиска:")
    return readLine()!!
}

private fun printResult(results:  List<SearchResult>) {
    if (results.isEmpty()) {
        println("Строка не найдена")
        return
    }
    println("Искомая строка найдена в следующих документах:")
    results.forEach {
        println(it.file.name)
        it.occurrences.forEach {
            println("позиция: ${it.first().start}")
        }
        println()
    }
}