package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        switch (type) {
            case KING:
                // King can move 1 in any direction
                addValidMove(validMoves, x+1, y);
                addValidMove(validMoves, x-1, y);
                addValidMove(validMoves, x, y-1);
                addValidMove(validMoves, x, y+1);
                addValidMove(validMoves, x+1, y+1);
                addValidMove(validMoves, x+1, y-1);
                addValidMove(validMoves, x-1, y+1);
                addValidMove(validMoves, x-1, y-1);
                break;
            case BISHOP:
                // Bishop moves diagonally any number of squares
                for (int i = 1; i < 8; i++) {
                    addValidMove(validMoves, x + i, y + i);
                    addValidMove(validMoves, x - i, y - i);
                    addValidMove(validMoves, x + i, y - i);
                    addValidMove(validMoves, x - i, y + i);
                }
                break;
        }
        return validMoves;
    }

    private void addValidMove(Collection<ChessMove> moves, int x, int y) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            moves.add(new ChessMove(new ChessPosition(x, y)));
        }
    }
}