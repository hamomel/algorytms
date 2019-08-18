private const val HEADER = "  +-----------------+"
private const val VERTICALS = "    a b c d e f g h"

data class Board(
    var disposition: Array<Array<Piece?>>,
    val isWhiteNext: Boolean,
    val whiteCastlings: Castling,
    val blackCastlings: Castling,
    val beatenPath: Cell?,
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

            val lines = disposition.split("/").reversed()
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

            val beatenPath = if (onPass.isBlank() || onPass == "-") {
                null
            } else {
                Cell.fromString(onPass)
            }

            return Board(
                disposition = board,
                isWhiteNext = whoMoves.toLowerCase() == "w",
                whiteCastlings = Castling(castling.contains("K"), castling.contains("Q")),
                blackCastlings = Castling(castling.contains("k"), castling.contains("q")),
                beatenPath = beatenPath,
                emptyMoves = emptyMove.trim().toInt(),
                moveNum = moveNum.toInt()
            )
        }
    }

    fun getPieceOnCell(cell: Cell): Piece? = disposition[cell.number][cell.letter]

    fun move(from: Cell, to: Cell) {
        disposition[to.number][to.letter] = getPieceOnCell(from)
    }

    fun toFen(): String =
        buildString {
            disposition.reversed().forEachIndexed { index, line ->
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
            append(if (isWhiteNext) "w " else "b ")

            if (!whiteCastlings.isShort &&
                !whiteCastlings.isLong &&
                !blackCastlings.isShort &&
                !blackCastlings.isLong
            ) {
                append("-")
            } else {
                if (whiteCastlings.isShort) append("K")
                if (whiteCastlings.isLong) append("Q")
                if (blackCastlings.isShort) append("k")
                if (blackCastlings.isLong) append("q")
            }
            append(" ")
            append(beatenPath?.let { it } ?: "-")
            append(" ")
            append(emptyMoves)
            append(" ")
            append(moveNum)
        }

    override fun toString(): String =
        buildString {
            append(HEADER)
            append("\n")
            disposition.reversed().forEachIndexed { index, line ->
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

        fun isSameType(other: Piece): Boolean =
            other.letter.toLowerCase() == this.letter.toLowerCase()

        companion object {
            fun getByLetter(letter: Char): Piece = values().first { it.letter == letter }
        }
    }

    data class Castling(val isShort: Boolean, val isLong: Boolean)
}