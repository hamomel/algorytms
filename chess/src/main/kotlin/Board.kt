private val HEADER = "  +-----------------+"
private val VERTICALS = "    a b c d e f g h"


data class Board(
    var disposition: Array<Array<Piece?>>
) {
    companion object {
        fun getEmpty() = Array(8) { arrayOfNulls<Piece?>(8) }
    }

    override fun toString(): String =
        buildString {
            append(HEADER)
            append("\n")
            disposition.forEachIndexed { index, line ->
                val lineNum = 8 - index
                append("$lineNum | ")
                line.forEach { piece ->
                    append(
                        piece?.let { "${it.letter} " } ?: ". "
                    )
                }
                append("|")
                append("\n")
            }
            append(HEADER)
            append("\n")
            append(VERTICALS)
        }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!disposition.contentDeepEquals(other.disposition)) return false

        return true
    }

    override fun hashCode(): Int {
        return disposition.contentDeepHashCode()
    }

    enum class Piece(val letter: Char) {
        WHITE_PAWN('P'),
        WHITE_QUIN('Q'),
        WHITE_KING('K'),
        WHITE_ROOK('R'),
        WHITE_KNIGHT('N'),
        WHITE_BISHOP('B'),

        BLACK_PAWN('p'),
        BLACK_QUIN('q'),
        BLACK_KING('k'),
        BLACK_ROOK('r'),
        BLACK_KNIGHT('n'),
        BLACK_BISHOP('b');

        companion object {
            fun getByLetter(letter: Char): Piece = values().first { it.letter == letter }
        }
    }
}