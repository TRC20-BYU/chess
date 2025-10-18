package dataaccess;

import dataModel.UserData;

public interface DataAccess {

    public void saveUser(UserData user);

    public void getUser(String username);
}
