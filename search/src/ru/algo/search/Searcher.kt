package ru.algo.search

import java.io.File

class Searcher(private val files: Array<File>, private val index: Array<Array<Array<Int>>?>) {

    fun search(phrase: String): List<SearchResult> {
        val parts = removeSigns(phrase)
        val words = parts.filter { it.length >= MIN_WORD_LENGTH && it.isNotBlank() }
        val occurrences = words.map {
            val hash = calculateHash(it.toByteArray())
            return@map index[hash] ?: emptyArray()
        }

        // indices of files in which all words are present
        val uniqueFileIndices = mutableSetOf<Int>()
        occurrences.forEach { occurrence ->
            val fileIndices = occurrence.map { it[0] }
            if (uniqueFileIndices.isEmpty()) {
                uniqueFileIndices.addAll(fileIndices)
            } else {
                uniqueFileIndices.retainAll(fileIndices)
            }
        }

        // map file names to list of indices of occurrences each word in the phrase in order of words in phrase
        val splitByFile = mutableMapOf<File, MutableList<List<Int>>>()
        occurrences.map { array ->
            array.filter { uniqueFileIndices.contains(it[0]) }
        }.forEach { occurrence ->
            occurrence.forEach {
                val fileName = files[it[0]]
                if (splitByFile[fileName] == null) {
                    splitByFile[fileName] = mutableListOf(it.drop(1))
                } else {
                    splitByFile[fileName]?.add(it.drop(1))
                }
            }
        }

        val resultWithIndices = searchIndices(words, splitByFile)

        return searchPhrases(resultWithIndices)
    }

    private fun searchIndices(
        words: List<String>,
        splitByFile: MutableMap<File, MutableList<List<Int>>>
    ): Map<File, List<List<WordCoordinates>>> =
        if (words.size < 2) {
            splitByFile.mapValues {
                it.value[0].map { listOf(WordCoordinates(it, it + words[0].length)) }
            }
        } else {
            // map file names to sequence of indices of phrase's words in it
            splitByFile.mapValues { entry ->
                val wordIndices = entry.value
                val sequences = mutableListOf<List<WordCoordinates>>()
                var sequence = mutableListOf<WordCoordinates>()

                for (i in wordIndices[0].indices) {
                    findNextWord(0, i, wordIndices, words, sequence, MAX_WORD_DISTANCE)
                    if (sequence.isNotEmpty()) sequences.add(sequence)
                    sequence = mutableListOf()
                }
                return@mapValues sequences
            }.filter {
                it.value.isNotEmpty()
            }
        }

    private fun searchPhrases(resultWithIndices: Map<File, List<List<WordCoordinates>>>): List<SearchResult> {
        val result = mutableListOf<SearchResult>()
        resultWithIndices.forEach { searchResult ->
            val phrases = mutableListOf<Phrase>()
            var position = 0
            val phraseCoordinates = searchResult.value.map {
                val start = it.first().start
                val end = it.last().end
                start to end - start
            }

            var remainedLength = 0
            var previousPhrase: Phrase? = null
            searchResult.key.useLines { lines ->
                lines.forEachIndexed { index,  line ->
                    if (remainedLength > 0) {
                        previousPhrase?.let {
                            val end = if (remainedLength <= line.length) remainedLength else line.length
                            val newPhrase = it.copy(text = it.text + line.substring(0 until end))
                            phrases.add(newPhrase)
                        }
                        remainedLength = 0
                        previousPhrase = null
                    }
                    phraseCoordinates.forEach { phraseCoordinates ->
                        if (position <= phraseCoordinates.first && position + line.length > phraseCoordinates.first) {
                            val start = phraseCoordinates.first - position
                            if (line.length - start >= phraseCoordinates.second) {
                                val phrase = Phrase(
                                    line = index + 1,
                                    position = start,
                                    text = line.substring(start, start + phraseCoordinates.second)
                                )
                                phrases.add(phrase)
                            } else {
                                remainedLength = phraseCoordinates.second - (line.length - start)
                                previousPhrase = Phrase(
                                    line = index + 1,
                                    position = start,
                                    text = line.substring(start)
                                )
                            }
                        }
                    }
                    position += (line.length + 1) // we need to add 1 because BufferedReader removes "\n" from line
                }
            }

            result.add(SearchResult(searchResult.key, phrases))
        }

        return result
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