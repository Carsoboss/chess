package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn;
    private ChessBoard board;

    public ChessGame() {
        this.currentTurn = TeamColor.WHITE; // Default to white starting
        this.board = new ChessBoard(); // Initialize the board
        this.board.resetBoard(); // Reset to default starting positions
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>(possibleMoves);

        validMoves.removeIf(move -> leavesKingInCheck(piece, move));

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        validateMove(piece, startPosition, move);

        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null); // Clear the start position

        if (isKingInCheck(currentTurn)) {
            revertMove(startPosition, endPosition, piece);
            throw new InvalidMoveException("Move leaves king in check");
        }

        // Handle pawn promotion
        handlePawnPromotion(move, endPosition);

        // Switch turns
        switchTurns();
    }

    private void validateMove(ChessPiece piece, ChessPosition startPosition, ChessMove move) throws InvalidMoveException {
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        // Check if the move is made by the correct team
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> validMoves = validMoves(startPosition);

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
    }

    private void revertMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece piece) {
        board.addPiece(startPosition, piece);
        board.addPiece(endPosition, null);
    }

    private void handlePawnPromotion(ChessMove move, ChessPosition endPosition) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(endPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 1 || endPosition.getRow() == 8)) {
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            if (promotionType == null) {
                throw new InvalidMoveException("Pawn promotion type required");
            }
            board.addPiece(endPosition, new ChessPiece(currentTurn, promotionType));
        }
    }

    private void switchTurns() {
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private boolean leavesKingInCheck(ChessPiece piece, ChessMove move) {
        ChessBoard copyBoard = copyBoardWithMove(move);
        ChessGame copyGame = new ChessGame();
        copyGame.setBoard(copyBoard);
        return copyGame.isInCheck(piece.getTeamColor());
    }

    private boolean isKingInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false; // King not on the board, can't be in check
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    if (canMoveToPosition(piece, new ChessPosition(row, col), kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canMoveToPosition(ChessPiece piece, ChessPosition startPosition, ChessPosition targetPosition) {
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(targetPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null; // King not found on the board
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isKingInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (hasValidMoveForPiece(piece, new ChessPosition(row, col), teamColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasValidMoveForPiece(ChessPiece piece, ChessPosition position, TeamColor teamColor) {
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        for (ChessMove move : moves) {
            ChessBoard copyBoard = copyBoardWithMove(move);
            ChessGame copyGame = new ChessGame();
            copyGame.setBoard(copyBoard);
            if (!copyGame.isInCheck(teamColor)) {
                return true;
            }
        }
        return false;
    }

    private ChessBoard copyBoardWithMove(ChessMove move) {
        ChessBoard newBoard = copyBoard();
        newBoard.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        newBoard.addPiece(move.getStartPosition(), null);
        return newBoard;
    }

    private ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                newBoard.addPiece(new ChessPosition(row, col), board.getPiece(new ChessPosition(row, col)));
            }
        }
        return newBoard;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
