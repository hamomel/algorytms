object Pow {

    fun multiply(base: Double, pow: Int): Double {
        var res = 1.0
        for (i in 1..pow) {
            res *= base
        }

        return res
    }

    fun twoPower(base: Double, pow: Int): Double {
        if (pow == 0) return  1.0

        var res = base
        var twoPower = 2

        while (twoPower < pow) {
            res *= res
            twoPower *= 2
        }


        if (pow != twoPower) {
            for (i in 1..(pow - twoPower / 2))
                res *= base
        }

        return res
    }

    fun binary(base: Double, pow: Int): Double {
        var dividedPow = pow
        var res = 1.0
        var temp = base

        while (dividedPow >= 1) {
            temp *= temp
            dividedPow /= 2
            if (dividedPow % 2 == 1) {
                res *= temp
            }
        }

        if (pow % 2 == 1) res *= base

        return res
    }
}