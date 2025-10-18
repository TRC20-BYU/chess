package service;

import dataModel.RegistrationResult;
import dataModel.UserData;
import dataaccess.DataAccess;
import jakarta.servlet.Registration;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;


    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(UserData userData) {

        boolean saved = dataAccess.saveUser(userData);
        if (saved) {
            String authToken = generateToken();
            dataAccess.registerAuthToken(authToken, userData.username());
            return new RegistrationResult(userData.username(), authToken);
        }
        return null;

    }

    public RegistrationResult login(UserData loginCred) {
        UserData userData = dataAccess.getUser(loginCred.username());
        if (userData == null) {
            return null;
        }
        String newAuthToken = generateToken();
        dataAccess.registerAuthToken(newAuthToken, userData.username());
        return new RegistrationResult(userData.username(), newAuthToken);
    }

    public boolean logout(String authToken) {
        if (dataAccess.authenticate(authToken)) {
            dataAccess.removeAuthToken(authToken);
            return true;
        }
        return false;
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


}
