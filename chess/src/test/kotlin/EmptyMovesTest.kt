import org.junit.Assert
import org.junit.Test

class EmptyMovesTest {
    val cases = TestCases.getCasesFor("3694.1.Счётчик полуходов")

    @Test
    fun test_0() {
        testParser(0)
    }

    @Test
    fun test_1() {
        testParser(1)
    }

    @Test
    fun test_2() {
        testParser(2)
    }

    @Test
    fun test_3() {
        testParser(3)
    }

    @Test
    fun test_4() {
        testParser(4)
    }

    @Test
    fun test_5() {
        testParser(5)
    }

    @Test
    fun test_6() {
        testParser(6)
    }

    @Test
    fun test_7() {
        testParser(7)
    }

    @Test
    fun test_8() {
        testParser(8)
    }

    @Test
    fun test_9() {
        testParser(9)
    }

    private fun testParser(testNum: Int) {
        println("test $testNum")

        val case = cases.first { it.name == "test.$testNum.in" }
        val check = cases.first { it.name == "test.$testNum.out" }

        val input = case.readText()
        val out = check.readText()
        val board = Mover.fromFen(input)
        println(input)
        println(out)

        Assert.assertEquals(out.trimEnd(), board.toFen())
    }
}