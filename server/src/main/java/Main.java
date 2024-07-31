import chess.ChessPiece;
import chess.ChessGame;
import server.Server;


public class Main {
    public static void main(String[] args) {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        UserDataAccess userDataAccess = new MySQLUserStorage();
        AuthDataAccess authDataAccess = new MySQLAuthStorage();
        GameDataAccess gameDataAccess = new MySQLGameStorage();

        UserService userService = new UserService(userDataAccess, authDataAccess);
        GameService gameService = new GameService(authDataAccess, gameDataAccess);
        ClearService clearService = new ClearService(userDataAccess, authDataAccess, gameDataAccess);

        Server serverInstance = new Server(userService, gameService, clearService);
        serverInstance.run(8080);
    }
}
