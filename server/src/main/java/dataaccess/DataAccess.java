package dataaccess;

import dataModel.UserData;

public interface DataAccess {

    public boolean saveUser(UserData user);

    public UserData getUser(String username);
}
