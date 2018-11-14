package com.video.mapper;

import com.video.pojo.Videos;
import com.video.pojo.vo.VideosVO;
import com.video.utils.MyMapper;

import java.util.List;

public interface VideosVOMapper extends MyMapper<Videos> {

    public List<VideosVO> queryAllVideos();
}