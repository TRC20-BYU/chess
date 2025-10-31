package service;

import datamodel.AuthData;
import datamodel.UserData;
import dataaccess.DataAccess;
import server.ResponseException;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;


    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws ResponseException {

        boolean saved = dataAccess.saveUser(userData);
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
        if (!Objects.equals(userData.password(), loginCred.password())) {
            throw new ResponseException(ResponseException.Code.authError);
        }
        String newAuthToken = generateToken();
        dataAccess.registerAuthToken(newAuthToken, userData.username());
        return new AuthData(userData.username(), newAuthToken);
    }

    public boolean logout(String authToken) {
        if (dataAccess.authenticate(authToken)) {
            dataAccess.removeAuthToken(authToken);
            return true;
        }
        return false;
    }

    public void deleteDatabase() {
        dataAccess.deleteDatabase();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


}
