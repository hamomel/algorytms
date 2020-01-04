package ru.algo.search

import java.io.File

data class SearchResult(
    val file: File,
    // sequence of start and end indices for every word in every occurrence of the phrase in the file
    val occurrences: List<List<WordCoordinates>>,
    var phrases: Map<Int, String> = emptyMap()
)

data class WordCoordinates(val start: Int, val end: Int)