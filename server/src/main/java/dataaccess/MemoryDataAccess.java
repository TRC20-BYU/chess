package dataaccess;

import dataModel.UserData;
import dataaccess.DataAccess;


import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void getUser(String username) {
        users.get(username);
    }
}
