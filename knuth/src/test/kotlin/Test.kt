package test.kotlin

import junit.framework.TestCase.assertEquals
import main.kotlin.kmp
import main.kotlin.zSearch
import org.junit.Test
import java.util.*


class Test {

    @Test
    fun kmpTest() {
        val testCases = this.javaClass
            .getResourceAsStream("string_matching_test_cases_31272_751472-57251-751472.tsv")
        val scanner = Scanner(testCases)
        val expectedCount = Integer.parseInt(scanner.nextLine())

        var count = 0
        while (scanner.hasNext()) {
            val testString = scanner.nextLine().split("\t")
            val string = testString[0]
            val pattern = testString[1]
            val occurrences = ArrayList<Int>()
            if (testString.size == 3 && testString[2].isNotBlank()) {
                for (number in testString[2].split(" ")) {
                    occurrences.add(Integer.parseInt(number))
                }
            }

            val list = kmp(string, pattern)
            assertEquals(occurrences, list)
            count++
        }

        assertEquals(expectedCount, count)
    }

    @Test
    fun zSearchTest() {
        val testCases = this.javaClass
            .getResourceAsStream("string_matching_test_cases_31272_751472-57251-751472.tsv")
        val scanner = Scanner(testCases)
        val expectedCount = Integer.parseInt(scanner.nextLine())

        var count = 0
        while (scanner.hasNext()) {
            val testString = scanner.nextLine().split("\t")
            val string = testString[0]
            val pattern = testString[1]
            val occurrences = ArrayList<Int>()
            if (testString.size == 3 && testString[2].isNotBlank()) {
                for (number in testString[2].split(" ")) {
                    occurrences.add(Integer.parseInt(number))
                }
            }

            val list = zSearch(string, pattern)
            assertEquals(occurrences, list)
            count++
        }

        assertEquals(expectedCount, count)
    }
}