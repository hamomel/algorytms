import Board.Piece

object Mover {

    fun fromFen(fen: String): Board {
        val (boardFen, move) = fen.split("\n")
        var board = Board.fromFen(boardFen)

        if (move.isNotBlank()) {
           board = makeMove(board, move)
        }

        return board
    }

    private fun makeMove(board: Board, move: String): Board {
        val cellStrings = move.chunked(2)
        val from = Cell.fromString(cellStrings[0])
        val to = Cell.fromString(cellStrings[1])
        var emptyMoves = board.emptyMoves
        val movingPiece = board.getPieceOnCell(from)
        val beatenPiece = board.getPieceOnCell(to)

        if (beatenPiece != null ||
            movingPiece == Piece.WHITE_PAWN ||
            movingPiece == Piece.BLACK_PAWN) {
            emptyMoves = 0
        } else {
            emptyMoves++
        }

        return if (board.isWhiteNext) {
            board.copy(isWhiteNext = false, emptyMoves = emptyMoves)
        } else {
            board.copy(isWhiteNext = true, moveNum = board.moveNum + 1, emptyMoves = emptyMoves)
        }
    }
}