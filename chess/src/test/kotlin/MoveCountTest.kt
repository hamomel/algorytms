import org.junit.Assert.*
import org.junit.Test

class MoveCountTest {
    val cases = TestCases.getCasesFor("1746.1.Счётчик ходов")

    @Test
    fun test_0() {
        testMoves(0)
    }

    @Test
    fun test_1() {
        testMoves(1)
    }

    @Test
    fun test_2() {
        testMoves(2)
    }

    @Test
    fun test_3() {
        testMoves(3)
    }

    @Test
    fun test_4() {
        testMoves(4)
    }

    private fun testMoves(testNum: Int) {
        println("test $testNum")

        val case = cases.first { it.name == "test.$testNum.in" }
        val check = cases.first { it.name == "test.$testNum.out" }

        val input = case.readText()
        val out = check.readText()
        val board = Mover.fromFen(input)
        println(input)
        println(out)
        println(board)

        assertEquals(out.split(" ")[2], board.toFen().split(" ")[2])
        assertEquals(
            out.substringAfterLast(" ").trimEnd(),
            board.toFen().substringAfterLast(" ").trimEnd()
        )
    }
}