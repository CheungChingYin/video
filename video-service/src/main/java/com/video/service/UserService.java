package com.video.service;

import com.video.pojo.Users;

public interface UserService {

    public boolean queryUserNameIsExist(String userName);

    public void saveUser(Users user);

    /**
     * <p>Title:queryUserToLogin</p>
     *
     * <p>Description:用户登录，通过账号和密码查询用户是否存在</p>
     * @param username
     * @param password
     * @return
     */
    public Users queryUserToLogin(String username,String password);
}
