package dataaccess;

import dataModel.UserData;
import dataaccess.DataAccess;


import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public boolean saveUser(UserData user) {
        if (getUser(user.username()) != null) {
            return false;
        }
        users.put(user.username(), user);
        return true;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}
