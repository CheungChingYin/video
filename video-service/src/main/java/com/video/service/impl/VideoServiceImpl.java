package com.video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.video.mapper.VideosMapper;
import com.video.mapper.VideosVOMapper;
import com.video.pojo.SearchRecords;
import com.video.pojo.Videos;
import com.video.pojo.vo.VideosVO;
import com.video.service.VideoService;
import com.video.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosVOMapper videosVOMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {

        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);

        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideoCover(String videoId, String videoCoverPath) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(videoCoverPath);
        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllVideos(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosVOMapper.queryAllVideos();

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }
}
