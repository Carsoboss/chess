import chess.*;

public class Main {
    public static void main(String[] args) {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        server.Server serverInstance = new server.Server();
        serverInstance.run(8080);
    }
}
