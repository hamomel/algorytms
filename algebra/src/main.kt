import java.math.BigInteger
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    println("\n=========== NOD ============\n")

    val euclid = measureTimeMillis {
        println(Nod.euclid(425444180, 39))
    }

    val subtract = measureTimeMillis {
        println(Nod.subtract(425444180, 39))
    }

    println("euclid = $euclid ms, subtract = $subtract ms")

    println("\n=========== POW ============\n")

    println( "multiply: " +
        measureTimeMillis {
            println(Pow.multiply(2.0, 1000))

        }
    )
    println("twoPower: " +
        measureTimeMillis {
            println(Pow.twoPower(2.0, 1000))

        }
    )
    println("binary: " +
        measureTimeMillis {
            println(Pow.binary(2.0, 1000))

        }
    )

    println("\n=========== Prime Numbers ============\n")

    println(PrimeNum.enumerate(1_000_000))
    println(PrimeNum.array(1_000_000))
    println(PrimeNum.eratosfen(1_000_000))

    println("\n=========== Fibonacci ============\n")

    var result = 0L
    println(
        measureTimeMillis {
            result = Fibo.recursion(3)
        })

    println("recursion $result")

    var iteRres: BigInteger = BigInteger.ZERO
    println(
        measureTimeMillis {
            iteRres = Fibo.iterate(73)
        })

    println("iter $iteRres")

    var goldenRres: BigInteger = BigInteger.ZERO
    println(
        measureTimeMillis {
            result = Fibo.golden(73)
        })

    println("golden $result")
}