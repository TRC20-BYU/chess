package service;

import dataModel.RegistrationResult;
import dataModel.UserData;
import jakarta.servlet.Registration;

public class UserService {

    public RegistrationResult register(UserData userData) {
        return new RegistrationResult(userData.username(), "yzx");
    }
}
