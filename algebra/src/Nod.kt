
object Nod {

    fun subtract (first: Int, second: Int): Int {
        var first = first
        var second = second

        while (first != second) {
            if (first > second) {
                first -= second
            } else {
                second -= first
            }
        }

        return first
    }

    fun euclid(first: Int, second: Int): Int {
        var first = first
        var second = second

        while (first != 0 && second != 0) {
            if (first > second) {
                first %= second
            } else {
                second %= first
            }
        }
        return if (second == 0) first else second
    }
}
