package service;

import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;


    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws ResponseException {
        String securePassword = encrypt(userData.password());
        UserData secureUser = new UserData(userData.username(), securePassword, userData.email());
        boolean saved = dataAccess.saveUser(secureUser);
        if (saved) {
            String authToken = generateToken();
            dataAccess.registerAuthToken(authToken, userData.username());
            return new AuthData(userData.username(), authToken);
        }
        throw new ResponseException(ResponseException.Code.takenError);

    }

    public AuthData login(UserData loginCred) throws ResponseException {
        UserData userData = dataAccess.getUserData(loginCred.username());
        if (userData == null) {
            throw new ResponseException(ResponseException.Code.authError);
        }
        if (!checkEncryption(userData.password(), loginCred.password())) {
            throw new ResponseException(ResponseException.Code.authError);
        }
        String newAuthToken = generateToken();
        dataAccess.registerAuthToken(newAuthToken, userData.username());
        return new AuthData(userData.username(), newAuthToken);
    }

    public boolean logout(String authToken) throws ResponseException {
        if (dataAccess.authenticate(authToken)) {
            dataAccess.removeAuthToken(authToken);
            return true;
        } else {
            throw new ResponseException(ResponseException.Code.authError);
        }
    }

    public void deleteDatabase() throws ResponseException {
        dataAccess.deleteDatabase();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String encrypt(String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt());
    }

    private boolean checkEncryption(String encrypted, String password) {
        return BCrypt.checkpw(password, encrypted);
    }

}
