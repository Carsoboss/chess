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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
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
                addKingMoves(validMoves, x, y, myPosition, board);
                break;
            case QUEEN:
                addLinearMoves(validMoves, x, y, myPosition, board, true, true);
                break;
            case BISHOP:
                addLinearMoves(validMoves, x, y, myPosition, board, true, false);
                break;
            case KNIGHT:
                addKnightMoves(validMoves, x, y, myPosition, board);
                break;
            case ROOK:
                addLinearMoves(validMoves, x, y, myPosition, board, false, true);
                break;
            case PAWN:
                addPawnMoves(validMoves, x, y, myPosition, board);
                break;
        }
        return validMoves;
    }

    private void addKingMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board) {
        addValidMove(moves, x + 1, y, startPosition, board);
        addValidMove(moves, x - 1, y, startPosition, board);
        addValidMove(moves, x, y + 1, startPosition, board);
        addValidMove(moves, x, y - 1, startPosition, board);
        addValidMove(moves, x + 1, y + 1, startPosition, board);
        addValidMove(moves, x - 1, y - 1, startPosition, board);
        addValidMove(moves, x + 1, y - 1, startPosition, board);
        addValidMove(moves, x - 1, y + 1, startPosition, board);
    }

    private void addKnightMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board) {
        addValidMove(moves, x + 2, y + 1, startPosition, board);
        addValidMove(moves, x + 2, y - 1, startPosition, board);
        addValidMove(moves, x - 2, y + 1, startPosition, board);
        addValidMove(moves, x - 2, y - 1, startPosition, board);
        addValidMove(moves, x + 1, y + 2, startPosition, board);
        addValidMove(moves, x + 1, y - 2, startPosition, board);
        addValidMove(moves, x - 1, y + 2, startPosition, board);
        addValidMove(moves, x - 1, y - 2, startPosition, board);
    }

    private void addLinearMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, boolean diagonal, boolean straight) {
        if (straight) {
            addDirectionalMoves(moves, x, y, startPosition, board, 1, 0); // Down
            addDirectionalMoves(moves, x, y, startPosition, board, -1, 0); // Up
            addDirectionalMoves(moves, x, y, startPosition, board, 0, 1); // Right
            addDirectionalMoves(moves, x, y, startPosition, board, 0, -1); // Left
        }
        if (diagonal) {
            addDirectionalMoves(moves, x, y, startPosition, board, 1, 1); // Down-Right
            addDirectionalMoves(moves, x, y, startPosition, board, -1, -1); // Up-Left
            addDirectionalMoves(moves, x, y, startPosition, board, 1, -1); // Down-Left
            addDirectionalMoves(moves, x, y, startPosition, board, -1, 1); // Up-Right
        }
    }

    private void addDirectionalMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, int xIncrement, int yIncrement) {
        for (int i = 1; i < 8; i++) {
            if (!addValidMove(moves, x + i * xIncrement, y + i * yIncrement, startPosition, board)) {
                break;
            }
        }
    }

    private void addPawnMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        if (addValidPawnMove(moves, x + direction, y, startPosition, board, false)) {
            if (isPawnStartRow(x)) {
                addValidPawnMove(moves, x + 2 * direction, y, startPosition, board, false);
            }
        }

        addValidPawnMove(moves, x + direction, y + 1, startPosition, board, true);
        addValidPawnMove(moves, x + direction, y - 1, startPosition, board, true);

        if (isPromotionRow(x + direction)) {
            addPromotionMoves(moves, x + direction, y, startPosition, board, false);
            addPromotionMoves(moves, x + direction, y + 1, startPosition, board, true);
            addPromotionMoves(moves, x + direction, y - 1, startPosition, board, true);
        }
    }

    private boolean addValidMove(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board) {
        if (isOnBoard(x, y)) {
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (targetPiece == null || targetPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null));
                return targetPiece == null;
            }
        }
        return false;
    }

    private boolean addValidPawnMove(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, boolean isCapture) {
        if (isOnBoard(x, y)) {
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (!isCapture && targetPiece == null) {
                addPawnMoveOrPromotion(moves, x, y, startPosition);
                return true;
            } else if (isCapture && targetPiece != null && targetPiece.getTeamColor() != this.pieceColor) {
                addPawnMoveOrPromotion(moves, x, y, startPosition);
                return true;
            }
        }
        return false;
    }

    private void addPromotionMoves(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition, ChessBoard board, boolean isCapture) {
        if (isOnBoard(x, y)) {
            ChessPiece targetPiece = board.getPiece(new ChessPosition(x, y));
            if (!isCapture && targetPiece == null) {
                addAllPromotions(moves, startPosition, new ChessPosition(x, y));
            } else if (isCapture && targetPiece != null && targetPiece.getTeamColor() != this.pieceColor) {
                addAllPromotions(moves, startPosition, new ChessPosition(x, y));
            }
        }
    }

    private void addPawnMoveOrPromotion(Collection<ChessMove> moves, int x, int y, ChessPosition startPosition) {
        if (isPromotionRow(x)) {
            addAllPromotions(moves, startPosition, new ChessPosition(x, y));
        } else {
            moves.add(new ChessMove(startPosition, new ChessPosition(x, y), null));
        }
    }

    private void addAllPromotions(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
    }

    private boolean isOnBoard(int x, int y) {
        return x >= 1 && x <= 8 && y >= 1 && y <= 8;
    }

    private boolean isPawnStartRow(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 2) ||
                (pieceColor == ChessGame.TeamColor.BLACK && row == 7);
    }

    private boolean isPromotionRow(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 8) ||
                (pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }
}
