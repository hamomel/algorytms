import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.roundToLong

object Fibo {

    fun recursion(number: Int): Long =
        if (number < 3) {
            1
        } else {
            recursion(number - 2) + recursion(number - 1)
        }


    fun iterate(number: Int): BigInteger {
        var f1 = BigInteger.ZERO
        var f2 = BigInteger.ONE
        var f3: BigInteger
        for (i in 2..number) {
            f3 = f1 + f2
            f1 = f2
            f2 = f3
        }

        return f2
    }

    fun golden(number: Int): Long{
        val sqrt = Math.sqrt(5.0)
        val fi = (sqrt + 1) / 2

        return Math.round(Pow.binary(fi, number) / sqrt)
    }
}