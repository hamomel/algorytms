package main.kotlin

fun main() {
    println("hello")
}

fun kmp(text: String, pattern: String): List<Int> {
    val matches = mutableListOf<Int>()
    val table = computePrefix(pattern)
    var shift = 0

    for (index in text.indices) {
       while (shift > 0 && text[index] != pattern[shift])
           shift = table[shift - 1]

        if (text[index] == pattern[shift])
            shift++

        if (shift == pattern.length) {
            matches.add(index - pattern.length + 1)
            shift = table[shift - 1]
        }
    }

    return matches
}

fun computePrefix(pattern: String): Array<Int> {
    val prefixes = Array(pattern.length) { 0 }
    var k = 0

    for (index in 1 until prefixes.size) {
        while (k > 0 && pattern[k] != pattern[index])
            k = prefixes[k - 1]

        if (pattern[k] == pattern[index])
            k++

        prefixes[index] = k
    }

    return prefixes
}

fun zSearch(string: String, pattern: String): List<Int> {
    val matches = mutableListOf<Int>()
    val newString = "$pattern\t$string"
    val prefixes = Array(newString.length) { 0 }
    var k = 0

    for (index in 1 until newString.length) {
        while (k > 0 && newString[k] != newString[index])
            k = prefixes[k - 1]

        if (newString[k] == newString[index])
            k++

        prefixes[index] = k

        if (k == pattern.length)
            matches.add(index - pattern.length * 2)
    }

    return matches
}