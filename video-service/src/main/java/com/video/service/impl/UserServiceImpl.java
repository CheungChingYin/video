package com.video.service.impl;

import com.video.mapper.UsersMapper;
import com.video.pojo.Users;
import com.video.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExist(String userName) {

        Users user = new Users();
        user.setUsername(userName);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {

        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }
}
