package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.UserData;
import org.jetbrains.annotations.Nullable;
import server.ResponseException;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DBMemoryAccess implements DataAccess {

    public DBMemoryAccess() {
        configureDatabase();
    }

    @Override
    public boolean saveUser(UserData user) throws ResponseException {
        if (getUserData(user.username()) == null) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            String username = user.username();
            String password = user.password();
            String email = user.email();
            try {
                executeUpdate(statement, username, password, email);
                return true;
            } catch (DataAccessException ex) {
                throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
            }
        }
        return false;
    }

    @Override
    public UserData getUserData(String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUserData(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
        return null;
    }

    @Override
    public UserData getUsername(String authToken) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return getUserData(readAuthData(rs));
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
        return null;

    }

    /// maybe changes this to be a bool for pass fail??????????????????????????
    @Override
    public void registerAuthToken(String authToken, String username) throws ResponseException {
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }

    }

    @Override
    public boolean authenticate(String authToken) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
        return false;
    }

    @Override
    public void removeAuthToken(String authToken) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    @Override
    public void deleteDatabase() throws ResponseException {
        var statement = "TRUNCATE  TABLE users";
        try {
            executeUpdate(statement);
        } catch (DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
        statement = "TRUNCATE TABLE authTokens";
        try {
            executeUpdate(statement);
        } catch (DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
        statement = "TRUNCATE TABLE games";
        try {
            executeUpdate(statement);
            configureDatabase();
        } catch (DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        try {
            var serializer = new Gson();
            ChessGame chessGame = new ChessGame();
            String game = serializer.toJson(chessGame);
            return executeUpdate(statement, gameName, game);
        } catch (DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whitePlayerName, blackPlayerName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                GameData rs = readGames(gameID, ps);
                if (rs != null) {
                    return rs;
                }
            }
        } catch (SQLException | DataAccessException ex) {
            return null;
        }
        return null;
    }

    @Nullable
    private GameData readGames(int gameID, PreparedStatement ps) throws SQLException {
        ps.setInt(1, gameID);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return readGameData(rs);
            }
        }
        return null;
    }

    @Override
    public List<GameData> gamesList() {
        ArrayList<GameData> listOfGames = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whitePlayerName, blackPlayerName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                listGames(ps, listOfGames);
            }
        } catch (SQLException | DataAccessException ex) {
            return listOfGames;
        }
        return listOfGames;
    }

    private void listGames(PreparedStatement ps, ArrayList<GameData> listOfGames) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listOfGames.add(readGameData(rs));
            }
        }
    }

    @Override
    public void setWhite(int gameID, String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whitePlayerName FROM games WHERE gameID=?";
            try (PreparedStatement ps1 = conn.prepareStatement(statement)) {
                ps1.setInt(1, gameID);
                try (ResultSet rs = ps1.executeQuery()) {
                    updateWhitePlayer(gameID, username, rs, conn);
                }
            }

        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    private static void updateWhitePlayer(int gameID, String username, ResultSet rs, Connection conn) throws SQLException, ResponseException {
        String statement;
        if (rs.next()) {
            String whitePlayerName = rs.getString("whitePlayerName");
            if (whitePlayerName != null && username != null) {
                throw new ResponseException(ResponseException.Code.takenError);
            }
            statement = "UPDATE games Set whitePlayerName=? WHERE gameID=?";
            try (PreparedStatement ps2 = conn.prepareStatement(statement)) {
                ps2.setString(1, username);
                ps2.setInt(2, gameID);
                ps2.executeUpdate();
            }
        } else {
            throw new ResponseException(ResponseException.Code.requestError);
        }
    }

    @Override
    public void setBlack(int gameID, String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT blackPlayerName FROM games WHERE gameID=?";
            try (PreparedStatement ps1 = conn.prepareStatement(statement)) {
                ps1.setInt(1, gameID);
                try (ResultSet rs = ps1.executeQuery()) {
                    updateBlackPlayer(gameID, username, rs, conn);
                }
            }

        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    @Override
    public void updateGame(int gameId, ChessGame chessGame) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM games WHERE gameID=?";
            try (PreparedStatement ps1 = conn.prepareStatement(statement)) {
                ps1.setInt(1, gameId);
                try (ResultSet rs = ps1.executeQuery()) {
                    var serializer = new Gson();
                    String serializedGame = serializer.toJson(chessGame);
                    updateGameData(gameId, serializedGame, rs, conn);
                }
            }

        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    private static void updateGameData(int gameID, String serializedGame, ResultSet rs, Connection conn) throws SQLException, ResponseException {
        String statement;
        if (rs.next()) {
            statement = "UPDATE games Set game=? WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, serializedGame);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } else {
            throw new ResponseException(ResponseException.Code.requestError);
        }
    }

    private static void updateBlackPlayer(int gameID, String username, ResultSet rs, Connection conn) throws SQLException, ResponseException {
        String statement;
        if (rs.next()) {
            String blackPlayerName = rs.getString("blackPlayerName");
            if (blackPlayerName != null && username != null) {
                throw new ResponseException(ResponseException.Code.takenError);
            }
            statement = "UPDATE games Set blackPlayerName=? WHERE gameID=?";
            try (PreparedStatement ps2 = conn.prepareStatement(statement)) {
                ps2.setString(1, username);
                ps2.setInt(2, gameID);
                ps2.executeUpdate();
            }
        } else {
            throw new ResponseException(ResponseException.Code.requestError);
        }
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException, ResponseException {

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  authTokens(
              `authToken` varchar(128) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  games(
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(45) NOT NULL,
              `whitePlayerName` varchar(128),
              `blackPlayerName` varchar(128),
              `game` longtext,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()) {
                for (String statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }

            }
        } catch (SQLException | DataAccessException ex) {
            //do nothing for now
        }

    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private String readAuthData(ResultSet rs) throws SQLException {
        return rs.getString("username");
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String gameName = rs.getString("gameName");
        String whitePlayerName = rs.getString("whitePlayerName");
        String blackPlayerName = rs.getString("blackPlayerName");
        String game = rs.getString("game");
        var serializer = new Gson();
        ChessGame chessGame = serializer.fromJson(game, ChessGame.class);
        return new GameData(gameID, whitePlayerName, blackPlayerName, gameName, chessGame);
    }


}
