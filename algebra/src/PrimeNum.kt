import java.util.*
import kotlin.system.measureTimeMillis

object PrimeNum {
    fun enumerate(number: Int): Int {
        var res = 2
        println("enumerate: " +
            measureTimeMillis {
                for (i in 3..number) {
                    if (isPrime(i)) res++
                }
            } + " ms"
        )


        return res
    }

    private fun isPrime(number: Int): Boolean {
        var j = 1
        while (j * j < number) {
            j++
            if (number % j == 0) {
                return false
            }
        }
        return true
    }

    fun array(number: Int): Int {
        var res = 2
        println("array: " +
            measureTimeMillis {
                val primes = mutableListOf(2)
                for (i in 3..number) {
                    if (isPrimeInList(i, primes)) {
                        primes.add(i)
                        res++
                    }
                }
            } + " ms"
        )


        return res
    }

    private fun isPrimeInList(number: Int, primes: List<Int>): Boolean {
        primes.asSequence().forEach {
            if (number % it == 0) return false
            if (it * it > number) return true
        }

        return true
    }

    fun eratosfen(number: Int): Int {
        val set = BitSet()
        println( "eratosfen: " +
            measureTimeMillis {
                set.set(0, 2, false)
                set.set(2, number - 1)
                var current = 2
                var currentSquare = current * current

                while (currentSquare <= number) {
                    for (i in currentSquare until number step current) {
                        set.set(i, false)
                    }
                    current = set.nextSetBit(current + 1)
                    currentSquare = current * current
                }
            } + " ms")
        return set.cardinality()
    }
}