package com.video.controller;

import com.video.pojo.Users;
import com.video.service.UserService;
import com.video.utils.JSONResult;
import com.video.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
@RestController
public class RegistLoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/regist")
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
        return JSONResult.ok();
    }
}
