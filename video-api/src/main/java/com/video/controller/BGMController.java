package com.video.controller;

import com.video.service.BGMService;
import com.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "BGM业务接口", tags = {"BGM业务控制器"})
@RequestMapping("/bgm")
public class BGMController {

    @Autowired
    private BGMService bgmService;

    @ApiOperation(value = "获取BGM列表", notes = "获取BGM列表API")
    @PostMapping("/list")
    public JSONResult list() {
        return JSONResult.ok(bgmService.queryBGMList());
    }
}
