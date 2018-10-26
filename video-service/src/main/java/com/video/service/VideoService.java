package com.video.service;

import com.video.pojo.Videos;

public interface VideoService {

    public String saveVideo(Videos video);

    public void updateVideoCover(String videoId, String videoCoverPath);
}
