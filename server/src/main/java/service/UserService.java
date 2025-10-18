package service;

import dataModel.RegistrationResult;
import dataModel.UserData;
import dataaccess.DataAccess;
import jakarta.servlet.Registration;

public class UserService {

    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(UserData userData) {

        boolean saved = dataAccess.saveUser(userData);
        if (saved) {
            return new RegistrationResult(userData.username(), "yzx");
        }
        return null;

    }

}
