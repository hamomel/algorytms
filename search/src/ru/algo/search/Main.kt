package ru.algo.search

fun main(args: Array<String>) {
    val directory = args[0]
    val indexer = Indexer(directory)
    indexer.startIndex()
    val files = indexer.getFiles()
    val index = indexer.getIndex()
    val searcher = Searcher(files, index)

    while (true) {
        val input = promptUser()
        if (input.toLowerCase() == "exit") break
        val found = searcher.search(input)
        printResult(found)
    }
}

private fun promptUser(): String {
    println("Введите строку для поиска:")
    return readLine()!!
}

private fun printResult(results: List<Pair<String, Int>>) {
    println("Искомая строка найдена в следующих документах:")
    results.forEach {
        println("${it.first} позиция: ${it.second}")
    }
}