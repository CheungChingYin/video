package com.video.service.impl;

import com.video.mapper.BgmMapper;
import com.video.pojo.Bgm;
import com.video.service.BGMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BGMServiceImpl implements BGMService {

    @Autowired
    private BgmMapper bgmMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBGMList() {
        return bgmMapper.selectAll();
    }
}
