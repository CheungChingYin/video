package com.video.service;

import com.video.pojo.Videos;
import com.video.pojo.vo.VideosVO;
import com.video.utils.PagedResult;

import java.util.List;

public interface VideoService {

    String saveVideo(Videos video);

    void updateVideoCover(String videoId, String videoCoverPath);

    PagedResult getAllVideos(Integer page,Integer pageSize);


}
