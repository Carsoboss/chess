package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        switch (type) {
            case KING:
                addValidMove(validMoves, x + 1, y, myPosition, board);
                addValidMove(validMoves, x - 1, y, myPosition, board);
                addValidMove(validMoves, x, y + 1, myPosition, board);
                addValidMove(validMoves, x, y - 1, myPosition, board);
                addValidMove(validMoves, x + 1, y + 1, myPosition, board);
                addValidMove(validMoves, x - 1, y - 1, myPosition, board);
                addValidMove(validMoves, x + 1, y - 1, myPosition, board);
                addValidMove(validMoves, x - 1, y + 1, myPosition, board);
                break;
            case QUEEN:
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x + i, y, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x - i, y, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x, y + i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x, y - i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x + i, y + i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x - i, y - i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x + i, y - i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x - i, y + i, myPosition, board)) break;
                }
                break;
            case BISHOP:
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x + i, y + i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x - i, y - i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x + i, y - i, myPosition, board)) break;
                }
                for (int i = 1; i < 8; i++) {
                    if (!addValidMove(validMoves, x - i, y + i, myPosition, board)) break;
                }
                break;
            case KNIGHT:
                addValidMove(validMoves, x + 2, y + 1, myPosition, board);
                addValidMove(validMoves, x + 2, y - 1, myPosition, board);
                addValidMove(validMoves, x - 2, y + 1, myPosition, board);
                addValidMove(validMoves, x - 2, y - 1, myPosition, board);
                addValidMove(validMoves, x + 1, y + 2, myPosition, board);
                addValidMove(validMoves, x + 1, y - 2, myPosition, board);
                addValidMove(validMoves, x - 1, y + 2, myPosition, board);
                addValidMove(validMoves, x - 1, y - 2, myPosition, board);
                break;
            case ROOK:
                for (int i = x + 1; i <= 8; i++) {
                    if (!addValidMove(validMoves, i, y, myPosition, board)) break;
                }
                for (int i = x - 1; i >= 1; i--) {
                    if (!addValidMove(validMoves, i, y, myPosition, board)) break;
                }
                for (int i = y + 1; i <= 8; i++) {
                    if (!addValidMove(validMoves, x, i, myPosition, board)) break;
                }
                for (int i = y - 1; i >= 1; i--) {
                    if (!addValidMove(validMoves, x, i, myPosition, board)) break;
                }
                break;
            case PAWN:
                int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
                // Single move forward
                if (addValidPawnMove(validMoves, x + direction, y, myPosition, board, false)) {
                    // Double move forward on initial position
                    if ((pieceColor == ChessGame.TeamColor.WHITE && x == 2) ||
                            (pieceColor == ChessGame.TeamColor.BLACK && x == 7)) {
                        addValidPawnMove(validMoves, x + 2 * direction, y, myPosition, board, false);
                    }
                }
                // Capture moves
                addValidPawnMove(validMoves, x + direction, y + 1, myPosition, board, true);
                addValidPawnMove(validMoves, x + direction, y - 1, myPosition, board, true);
                // Promotion check
                if ((pieceColor == ChessGame.TeamColor.WHITE && x == 7) ||
                        (pieceColor == ChessGame.TeamColor.BLACK && x == 2)) {
                    addPromotionMoves(validMoves, x + direction, y, myPosition, board, false);
                    addPromotionMoves(validMoves, x + direction, y + 1, myPosition, board, true);
                    addPromotionMoves(validMoves, x + direction, y - 1, myPosition, board, true);
                }
                break;
        }
        return validMoves;
    }

    private boolean addValidMove(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board) {
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8) { // Check if the position is within the board bounds
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (targetPiece == null) { // If the target position is empty
                moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null)); // Add the move to the valid moves
                return true; // Continue adding moves in this direction
            } else if (targetPiece.getTeamColor() != this.pieceColor) { // If the target position has an opponent's piece
                moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null)); // Add the capture move to the valid moves
                return false; // Stop adding moves in this direction since a capture occurred
            } else { // If the target position has a friendly piece
                return false; // Stop adding moves in this direction
            }
        }
        return false; // Stop if the position is outside the board bounds
    }

    private boolean addValidPawnMove(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, boolean isCapture) {
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8) {
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (!isCapture && targetPiece == null) {
                if (isPromotionRow(x)) {
                    addAllPromotions(moves, startPosition, new ChessPosition(x, y));
                } else {
                    moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null));
                }
                return true;
            } else if (isCapture && targetPiece != null && targetPiece.getTeamColor() != this.pieceColor) {
                if (isPromotionRow(x)) {
                    addAllPromotions(moves, startPosition, new ChessPosition(x, y));
                } else {
                    moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null));
                }
                return true;
            }
        }
        return false;
    }

    private void addPromotionMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, boolean isCapture) {
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8) {
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (!isCapture && targetPiece == null) {
                addAllPromotions(moves, startPosition, new ChessPosition(x, y));
            } else if (isCapture && targetPiece != null && targetPiece.getTeamColor() != this.pieceColor) {
                addAllPromotions(moves, startPosition, new ChessPosition(x, y));
            }
        }
    }

    private void addAllPromotions(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
    }

    private boolean isPromotionRow(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 8) ||
                (pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }
}