package com.video.controller;

import com.video.pojo.Users;
import com.video.pojo.com.video.pojo.vo.UsersVO;
import com.video.service.UserService;
import com.video.utils.JSONResult;
import com.video.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
@RestController
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/regist")
    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    public JSONResult regist(@RequestBody Users user) throws Exception {

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名和密码不能为空");
        }
        boolean userIsExist = userService.queryUserNameIsExist(user.getUsername());
        if (!userIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
        } else {
            return JSONResult.errorMsg("用户名已存在，请更换一个！");
        }
        UsersVO userVO = setUserRedisSessionToken(user);
        return JSONResult.ok(userVO);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/login")
    public JSONResult login(@RequestBody Users user) throws Exception {

        String username = user.getUsername();
        String password = user.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return JSONResult.ok("用户名不能为空");
        }
        Users userResult = userService.queryUserToLogin(username, MD5Utils.getMD5Str(password));
        if (userResult != null) {
            userResult.setPassword("");
            UsersVO userVO = setUserRedisSessionToken(userResult);
            return JSONResult.ok(userVO);
        } else {
            return JSONResult.errorMsg("用户名或密码不正确，请重试!");
        }
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name="userId", value="用户Id", required=true, dataType="String", paramType="query")
    @PostMapping("/logout")
    public JSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return JSONResult.ok();
    }

    public UsersVO setUserRedisSessionToken(Users userModel){
        String uniqueToken = UUID.randomUUID().toString();
        // 在Redis中设置唯一的用户Id
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 60 * 30);


        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userModel, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }


}
