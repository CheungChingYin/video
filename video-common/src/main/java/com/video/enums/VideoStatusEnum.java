package com.video.enums;

public enum VideoStatusEnum {

    SUCCESS(1),//发布成功
    FORBID(2);//精致播放，管理员操作

    public final int value;

    VideoStatusEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
