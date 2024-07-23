import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // Create a Server object
        Server server = new Server();

        // Run the server on port 8080
        server.run(8080);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}
