package ru.algo.search

import java.io.File

class Searcher(private val files: Array<File>, private val index: Array<Array<Array<Int>>?>) {

    fun search(phrase: String): List<SearchResult> {
        val words = tokenize(phrase).filter { it.length >= MIN_WORD_LENGTH }
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

        // get rid of elements which doesn't represent all words of the phrase in each file
        occurrences.forEach { array ->
            array.filter { uniqueFileIndices.contains(it[0]) }
        }

        // map file names to list of indices of occurrences each word in the phrase in order of words in phrase
        val splitByFile = mutableMapOf<File, MutableList<List<Int>>>()
        occurrences.forEach { indexForWord ->
            indexForWord.forEach {
                val fileName = files[it[0]]
                if (splitByFile[fileName] == null) {
                    splitByFile[fileName] = mutableListOf(it.drop(1))
                } else {
                    splitByFile[fileName]?.add(it.drop(1))
                }
            }
        }

        if (words.size < 2) return splitByFile.map {
            val phrases = it.value[0].map { listOf(WordCoordinates(it, it + words[0].length)) }
            SearchResult(it.key, phrases)
        }

        // map file names to sequence of indices of phrase's words in it

        return splitByFile.map { entry ->
            val wordIndices = entry.value
            val sequences = mutableListOf<List<WordCoordinates>>()
            var sequence = mutableListOf<WordCoordinates>()

            for (i in wordIndices[0].indices) {
                findNextWord(0, i, wordIndices, words, sequence)
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
        result: MutableList<WordCoordinates>
    ) {
        val nextWordIndex = wordIndex + 1
        if (indices.size == nextWordIndex) return

        val wordEnd = indices[wordIndex][indexInArray] + words[wordIndex].length
        val nextWordArray = indices[nextWordIndex].filter { it > wordEnd }

        if (nextWordArray.isNotEmpty()) {
            var nextWord = -1
            for (i in nextWordArray.indices) {
                if (nextWordArray[i] < wordEnd + MAX_WORD_DISTANCE) {
                    nextWord = i
                    val wordCoordinates = WordCoordinates(indices[wordIndex][indexInArray], wordEnd)
                    val nextWordEnd = nextWordArray[i] + words[nextWordIndex].length
                    val nextWordCoordinates = WordCoordinates(nextWordArray[i], nextWordEnd)
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
                    break
                }
            }

            if (nextWord == -1) {
                result.clear()
            } else {
                findNextWord(wordIndex + 1, nextWord, indices, words, result)
            }
        } else {
            result.clear()
        }
    }
}