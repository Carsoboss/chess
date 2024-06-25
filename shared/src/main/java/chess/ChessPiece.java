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

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
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
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
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
                addValidMove(validMoves, x + 1, y);
                addValidMove(validMoves, x - 1, y);
                addValidMove(validMoves, x, y + 1);
                addValidMove(validMoves, x, y - 1);
                addValidMove(validMoves, x + 1, y + 1);
                addValidMove(validMoves, x - 1, y - 1);
                addValidMove(validMoves, x + 1, y - 1);
                addValidMove(validMoves, x - 1, y + 1);
                break;
            case QUEEN:
                for (int i = 1; i < 8; i++) {
                    addValidMove(validMoves, x + i, y);
                    addValidMove(validMoves, x - i, y);
                    addValidMove(validMoves, x, y + i);
                    addValidMove(validMoves, x, y - i);
                    addValidMove(validMoves, x + i, y + i);
                    addValidMove(validMoves, x - i, y - i);
                    addValidMove(validMoves, x + i, y - i);
                    addValidMove(validMoves, x - i, y + i);
                }
                break;
            case BISHOP:
                for (int i = 1; i < 8; i++) {
                    addValidMove(validMoves, x + i, y + i);
                    addValidMove(validMoves, x - i, y - i);
                    addValidMove(validMoves, x + i, y - i);
                    addValidMove(validMoves, x - i, y + i);
                }
                break;
            case KNIGHT:
                addValidMove(validMoves, x + 2, y + 1);
                addValidMove(validMoves, x + 2, y - 1);
                addValidMove(validMoves, x - 2, y + 1);
                addValidMove(validMoves, x - 2, y - 1);
                addValidMove(validMoves, x + 1, y + 2);
                addValidMove(validMoves, x + 1, y - 2);
                addValidMove(validMoves, x - 1, y + 2);
                addValidMove(validMoves, x - 1, y - 2);
                break;
            case ROOK:
                for (int i = 1; i < 8; i++) {
                    addValidMove(validMoves, x + i, y);
                    addValidMove(validMoves, x - i, y);
                    addValidMove(validMoves, x, y + i);
                    addValidMove(validMoves, x, y - i);
                }
                break;
            case PAWN:
                int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
                addValidMove(validMoves, x + direction, y);
                if ((pieceColor == ChessGame.TeamColor.WHITE && x == 2) ||
                        (pieceColor == ChessGame.TeamColor.BLACK && x == 7)) {
                    addValidMove(validMoves, x + 2 * direction, y);
                }
                // Capturing moves
                addValidMove(validMoves, x + direction, y + 1);
                addValidMove(validMoves, x + direction, y - 1);
                break;
        }
        return validMoves;
    }

    private void addValidMove(Collection<ChessMove> moves, int x, int y) {
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8) {
            moves.add(new ChessMove(new ChessPosition(x, y)));
        }
    }
}
