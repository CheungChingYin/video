package com.video.service;

import com.video.pojo.Users;

public interface UserService {

    public boolean queryUserNameIsExist(String userName);

    public void saveUser(Users user);
}
