private const val HEADER = "  +-----------------+"
private const val VERTICALS = "    a b c d e f g h"

data class Board(
    var disposition: Array<Array<Piece?>>,
    val isWhiteNext: Boolean,
    val whiteCastlings: Array<Boolean>,
    val blackCastlings: Array<Boolean>,
    val beatenPath: String,
    val emptyMoves: Int,
    val moveNum: Int
) {
    companion object {
        fun getEmpty() = Array(8) { arrayOfNulls<Piece?>(8) }

        fun fromFen(fen: String): Board {
            val splitted = fen.split(" ")
            val (
                disposition,
                whoMoves,
                castling,
                onPass,
                emptyMove) = splitted
            val moveNum = splitted[5].trim()

            val lines = disposition.split("/")
            val board = Board.getEmpty()

            lines.forEachIndexed { index, line ->
                var position = 0
                val iter = line.toCharArray().iterator()
                while (position < 8) {
                    var newPosition = 8

                    if (iter.hasNext()) {
                        val sign = iter.nextChar()

                        if (!sign.isDigit()) {
                            board[index][position] = Board.Piece.getByLetter(sign)
                            position++
                            continue
                        } else {
                            newPosition = position + sign.toString().toInt()
                        }
                    }

                    for (i in position until newPosition) {
                        board[index][i] = null
                    }
                    position = newPosition
                }
            }

            return Board(
                disposition = board,
                isWhiteNext = whoMoves.toLowerCase() == "w",
                whiteCastlings = arrayOf(castling.contains("K"), castling.contains("Q")),
                blackCastlings = arrayOf(castling.contains("k"), castling.contains("q")),
                beatenPath = onPass.trim(),
                emptyMoves = emptyMove.trim().toInt(),
                moveNum = moveNum.toInt())
        }

        fun toFen(board: Board): String =
            buildString {
                board.disposition.forEachIndexed { index, line ->
                    var spaces = 0
                    line.forEach {
                        it?.let {
                            if (spaces > 0) append(spaces)
                            append(it.letter)
                            spaces = 0
                        } ?: spaces++
                    }
                    if (spaces > 0) append(spaces)
                    if (index < 7) append("/")
                }
                append(" ")
                append(if (board.isWhiteNext) "w " else "b ")

                if (!board.whiteCastlings[0] &&
                    !board.whiteCastlings[1] &&
                    !board.blackCastlings[0] &&
                    !board.blackCastlings[1]) {
                    append("-")
                } else {
                    if (board.whiteCastlings[0]) append("K")
                    if (board.whiteCastlings[1]) append("Q")
                    if (board.blackCastlings[0]) append("k")
                    if (board.blackCastlings[1]) append("q")
                }
                append(" ")
                append(if (board.beatenPath.isNotEmpty()) board.beatenPath else "-")
                append(" ")
                append(board.emptyMoves)
                append(" ")
                append(board.moveNum)
            }
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