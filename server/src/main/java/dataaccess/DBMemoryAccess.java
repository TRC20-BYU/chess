package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.sql.Connection;
import java.sql.DriverManager;
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
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String username = user.username();
        String password = user.password();
        String email = user.email();
        try {
            executeUpdate(statement, username, password, email);
        } catch (DataAccessException e) {
            System.out.print("here2");
        }

        return true;
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
    public UserData getUsername(String authToken) {
        return null;
    }

    @Override
    public void registerAuthToken(String authToken, String username) {

    }

    @Override
    public boolean authenticate(String authToken) {
        return false;
    }

    @Override
    public void removeAuthToken(String authToken) {

    }

    @Override
    public void deleteDatabase() {
        var statement = "DROP DATABASE chess";
        try {
            executeUpdate(statement);
        } catch (DataAccessException e) {
            System.out.print("here2");
        }
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public List<GameData> gamesList() {
        return List.of();
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {

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
                ps.execute();
            }
        } catch (SQLException ex) {
//                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
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
}
