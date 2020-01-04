package ru.algo.search

import java.io.File

class Searcher(private val files: Array<File>, private val index: Array<Array<Array<Int>>?>) {

    fun search(phrase: String): List<SearchResult> {
        val parts = tokenize(phrase)
        val maxBlankWord = parts.filter { it.isBlank() || it.length < MIN_WORD_LENGTH }.maxBy { it.length }
        val maxDistance = MAX_WORD_DISTANCE
        val words = parts.filter { it.length >= MIN_WORD_LENGTH && it.isNotBlank() }
        val occurrences = words.map {
            val hash = calculateHash(it.toByteArray())
            return@map index[hash] ?: emptyArray()
        }

        // file indices in which all words are present
        val uniqueFileIndices = mutableSetOf<Int>()
        occurrences.forEachIndexed { i, indexForWord ->
            val fileIndices = indexForWord.map { it[0] }
            if (i == 0) {
                uniqueFileIndices.addAll(fileIndices)
            } else {
                uniqueFileIndices.retainAll(fileIndices)
            }
        }

        // map file names to list of indices of occurrences each word in the phrase in order of words in phrase
        val splitByFile = mutableMapOf<File, MutableList<List<Int>>>()
        occurrences.map { array ->
            array.filter { uniqueFileIndices.contains(it[0]) }
        }.forEach { indexForWord ->
            indexForWord.forEach {
                val fileName = files[it[0]]
                if (splitByFile[fileName] == null) {
                    splitByFile[fileName] = mutableListOf(it.drop(1))
                } else {
                    splitByFile[fileName]?.add(it.drop(1))
                }
            }
        }

        val resultWithIndices = searchIndices(words, splitByFile, maxDistance)
        return searchPhrases(resultWithIndices)
    }

    private fun searchPhrases(resultWithIndices: List<SearchResult>): List<SearchResult> {
        resultWithIndices.forEach { searchResult ->
            val phrases = mutableMapOf<Int, String>()
            var position = 0
            val phraseCoordinates = searchResult.occurrences.map {
                val start = it.first().start
                val end = it.last().end
                PhraseCoordinates(start, end, end - start)
            }
            var remainedLength = 0
            var previousCoordinates: PhraseCoordinates? = null
            searchResult.file.useLines { lines ->
                lines.forEach { line ->
                    if (remainedLength > 0) {
                        previousCoordinates?.let {
                            phrases[it.start] = phrases[it.start] + line.substring(0..remainedLength)
                        }
                        remainedLength = 0
                        previousCoordinates = null
                    }
                    phraseCoordinates.forEach {
                        if (position <= it.start && position + line.length > it.end) {
                            val start = it.start - position
                            if (line.length - start >= it.length) {
                                phrases[it.start] = line.substring(start, start + it.length)
                            } else {
                                remainedLength = it.length - (line.length - start)
                                previousCoordinates = it
                                phrases[it.start] = line.substring(it.start)
                            }
                        }
                    }
                    position += line.length
                }
            }

            searchResult.phrases = phrases
        }

        return resultWithIndices
    }

    private fun searchIndices(
        words: List<String>,
        splitByFile: MutableMap<File, MutableList<List<Int>>>,
        maxDistance: Int
    ): List<SearchResult> =
        if (words.size < 2) {
            splitByFile.map {
                val phrases = it.value[0].map { listOf(WordCoordinates(it, it + words[0].length)) }
                SearchResult(it.key, phrases)
            }
        } else {
            // map file names to sequence of indices of phrase's words in it
            splitByFile.map { entry ->
                val wordIndices = entry.value
                val sequences = mutableListOf<List<WordCoordinates>>()
                var sequence = mutableListOf<WordCoordinates>()

                for (i in wordIndices[0].indices) {
                    findNextWord(0, i, wordIndices, words, sequence, maxDistance)
                    if (sequence.isNotEmpty()) sequences.add(sequence)
                    sequence = mutableListOf()
                }
                return@map SearchResult(entry.key, sequences)
            }.filter {
                it.occurrences.isNotEmpty()
            }
        }

    private fun findNextWord(
        wordIndex: Int,
        indexInArray: Int,
        indices: MutableList<List<Int>>,
        words: List<String>,
        result: MutableList<WordCoordinates>,
        maxDistance: Int
    ) {
        val nextWordIndex = wordIndex + 1
        if (indices.size == nextWordIndex) return

        val wordEnd = indices[wordIndex][indexInArray] + words[wordIndex].length
        val nextWordStartIndex = indices[nextWordIndex].indexOfFirst { it > wordEnd }

        if (nextWordStartIndex >= 0 && wordEnd + maxDistance >= indices[nextWordIndex][nextWordStartIndex]) {
            val nextWordStart = indices[nextWordIndex][nextWordStartIndex]
            val wordCoordinates = WordCoordinates(indices[wordIndex][indexInArray], wordEnd)
            val nextWordEnd = nextWordStart + words[nextWordIndex].length
            val nextWordCoordinates = WordCoordinates(nextWordStart, nextWordEnd)
            if (result.size == wordIndex) {
                result.add(wordCoordinates)
            } else {
                result[wordIndex] = wordCoordinates
            }

            if (result.size == nextWordIndex) {
                result.add(nextWordCoordinates)
            } else {
                result[nextWordIndex] = nextWordCoordinates
            }

            findNextWord(wordIndex + 1, nextWordStartIndex, indices, words, result, maxDistance)
        } else {
            result.clear()
        }
    }
}