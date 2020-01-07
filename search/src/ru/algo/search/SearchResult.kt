package ru.algo.search

import java.io.File

data class SearchResult(
    val file: File,
    var phrases: List<Phrase> = emptyList()
)