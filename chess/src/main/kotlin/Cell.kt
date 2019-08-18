private const val A_CODE_ASCII  = 97

data class Cell(val letter: Int, val number: Int) {
    companion object {
        fun fromString(string: String): Cell {
            val parts = string.toLowerCase().chunked(1)
            return Cell(
                letter = parts[0].toCharArray()[0].toInt() - A_CODE_ASCII,
                number = parts[1].toInt() - 1
            )
        }
    }

    override fun toString(): String {
        return (letter + A_CODE_ASCII).toChar() + "" + (number + 1)
    }
}