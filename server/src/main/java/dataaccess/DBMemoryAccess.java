package dataaccess;

import datamodel.GameData;
import datamodel.UserData;
import server.ResponseException;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DBMemoryAccess implements DataAccess {

    public DBMemoryAccess() {

        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.print("here");
        }
    }

    @Override
    public boolean saveUser(UserData user) {
        if (getUserData(user.username()) == null) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            String username = user.username();
            String password = user.password();
            String email = user.email();
            try {
                executeUpdate(statement, username, password, email);
                return true;
            } catch (DataAccessException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public UserData getUserData(String username) {
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
            return null;
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
        throw new ResponseException(ResponseException.Code.authError);
    }

    /// maybe changes this to be a bool for pass fail??????????????????????????
    @Override
    public void registerAuthToken(String authToken, String username) {
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException e) {
            System.out.print("here3");
        }

    }

    @Override
    public boolean authenticate(String authToken) {
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
            return false;
        }
        return false;
    }

    @Override
    public void removeAuthToken(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            System.out.print("here4");
        }
    }

    @Override
    public void deleteDatabase() {
        var statement = "DROP DATABASE chess";
        try {
            executeUpdate(statement);
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.print("here2");
        }

    }

    @Override
    public int createGame(String gameName) {
        var statement = "INSERT INTO games (gameName) VALUES (?)";
        try {
            return executeUpdate(statement, gameName);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    @Override
    public GameData getGame(int gameID) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whitePlayerName, blackPlayerName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            return null;
        }
        return null;
    }

    @Override
    public List<GameData> gamesList() {
        ArrayList<GameData> listOfGames = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whitePlayerName, blackPlayerName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        listOfGames.add(readGameData(rs));
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            return listOfGames;
        }
        return listOfGames;
    }

    @Override
    public void setWhite(int gameID, String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whitePlayerName FROM games WHERE gameID=?";
            try (PreparedStatement ps1 = conn.prepareStatement(statement)) {
                ps1.setInt(1, gameID);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        String whitePlayerName = rs.getString("whitePlayerName");
                        if (whitePlayerName != null) {
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
            }

        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }

    @Override
    public void setBlack(int gameID, String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT blackPlayerName FROM games WHERE gameID=?";
            try (PreparedStatement ps1 = conn.prepareStatement(statement)) {
                ps1.setInt(1, gameID);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        String blackPlayerName = rs.getString("blackPlayerName");
                        if (blackPlayerName != null) {
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
            }

        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(ResponseException.Code.serverError, ex.getMessage());
        }
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {

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
//                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return 0;
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

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException ex) {
//                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
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
        return new GameData(gameID, whitePlayerName, blackPlayerName, gameName);
    }


}
