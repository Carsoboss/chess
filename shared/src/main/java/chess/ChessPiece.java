package chess;

import java.util.Collection;
import java.util.HashSet;
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
        Collection<ChessMove> moves = new HashSet<>();

        switch (type) {
            case KING:
                moves.addAll(calculateKingMoves(board, myPosition));
                break;
            case QUEEN:
                moves.addAll(calculateQueenMoves(board, myPosition));
                break;
            case BISHOP:
                moves.addAll(calculateBishopMoves(board, myPosition));
                break;
            case KNIGHT:
                moves.addAll(calculateKnightMoves(board, myPosition));
                break;
            case ROOK:
                moves.addAll(calculateRookMoves(board, myPosition));
                break;
            case PAWN:
                moves.addAll(calculatePawnMoves(board, myPosition));
                break;
        }

        return moves;
    }

    private Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int[][] directions = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.getRow() + direction[0];
            int newCol = myPosition.getColumn() + direction[1];
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (isValidMove(board, newPosition)) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        moves.addAll(calculateRookMoves(board, myPosition));
        moves.addAll(calculateBishopMoves(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (!isValidMove(board, newPosition)) break;
                moves.add(new ChessMove(myPosition, newPosition, null));
                if (board.getPiece(newPosition) != null) break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.getRow() + direction[0];
            int newCol = myPosition.getColumn() + direction[1];
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (isValidMove(board, newPosition)) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int[][] directions = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (!isValidMove(board, newPosition)) break;
                moves.add(new ChessMove(myPosition, newPosition, null));
                if (board.getPiece(newPosition) != null) break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        ChessPosition oneStep = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());

        if (isValidMove(board, oneStep) && board.getPiece(oneStep) == null) {
            moves.add(new ChessMove(myPosition, oneStep, null));
            ChessPosition twoSteps = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
            if (myPosition.getRow() == startRow && board.getPiece(twoSteps) == null) {
                moves.add(new ChessMove(myPosition, twoSteps, null));
            }
        }

        int[][] captures = {{direction, 1}, {direction, -1}};
        for (int[] capture : captures) {
            ChessPosition capturePosition = new ChessPosition(myPosition.getRow() + capture[0], myPosition.getColumn() + capture[1]);
            ChessPiece capturePiece = board.getPiece(capturePosition);
            if (isValidMove(board, capturePosition) && capturePiece != null && capturePiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, capturePosition, null));
            }
        }

        return moves;
    }

    private boolean isValidMove(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
