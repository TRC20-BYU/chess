package service;

import dataModel.AuthData;
import dataModel.UserData;
import dataaccess.DataAccess;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;


    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) {

        boolean saved = dataAccess.saveUser(userData);
        if (saved) {
            String authToken = generateToken();
            dataAccess.registerAuthToken(authToken, userData.username());
            return new AuthData(userData.username(), authToken);
        }
        return null;

    }

    public AuthData login(UserData loginCred) {
        UserData userData = dataAccess.getUserData(loginCred.username());
        if (userData == null) {
            return null;
        }
        if (!Objects.equals(userData.password(), loginCred.password())) {
            return null;
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
