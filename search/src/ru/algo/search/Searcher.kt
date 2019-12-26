package ru.algo.search

import java.io.File

class Searcher(val files: Array<File>, val index: Array<Array<Array<Int>>>) {

    fun search(phrase: String): List<Pair<String, Int>> {
        return listOf("first $phrase" to 345, "second $phrase" to 990)
    }
}