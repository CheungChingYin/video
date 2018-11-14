package com.video.controller;

import com.video.enums.VideoStatusEnum;
import com.video.pojo.Bgm;
import com.video.pojo.Videos;
import com.video.service.BGMService;
import com.video.service.VideoService;
import com.video.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

import static com.video.controller.BasicController.FFMPEG_EXE;
import static com.video.controller.BasicController.FILE_SPACE;
import static com.video.controller.BasicController.PAGE_SIZE;

@RestController
@Api(value = "视频业务处理", tags = {"视频业务处理控制器API"})
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private BGMService bgmService;

    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "上传视频", notes = "上传视频API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bGMId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")})
    @PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
    public JSONResult uploadVideo(String userId, String bGMId, double videoSeconds, int videoWidth, int videoHeight,
                                  String desc, @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户ID不能为空");
        }

        // // 文件保存的命名空间
        // String fileSpace = "d:/svideos_res";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        // 文件上传的最终保存路径
        String finalVideoPath = "";
        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                System.out.println("上传文件名：" + fileName);
                // abc.mp4
                String arrayFilenameItem[] = fileName.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }
                // fix bug: 解决小程序端OK，PC端不OK的bug，原因：PC端和小程序端对临时视频的命名不同
                // String fileNamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNoneBlank(fileName)) {
                    // 文件上传的最终保存路径
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return JSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        // 判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生产新的视频
        if (StringUtils.isNotBlank(bGMId)) {
            Bgm bgm = bgmService.queryBGMById(bGMId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
        }

        System.out.println("uploadPathDB" + uploadPathDB);
        System.out.println("finalVideoPath" + finalVideoPath);

        //对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bGMId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return JSONResult.ok(videoId);

    }

    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form")})
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public JSONResult uploadCover(String userId, String videoId, @ApiParam(value = "视频封面", required = true) MultipartFile file) throws IOException {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("视频主键id和用户id不能为空...");
        }

        String uploadPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {

                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return JSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        videoService.updateVideoCover(videoId, uploadPathDB);

        return JSONResult.ok();
    }

    /**
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     * @Description: 分页和搜索查询视频列表 isSaveRecord：1 - 需要保存 0 - 不需要保存 ，或者为空的时候
     */
    @ApiOperation(value = "获取视频列表", notes = "获取视频列表API")
    @ApiImplicitParam(name = "page", value = "第几页", required = false, dataType = "String", paramType = "query")
    @PostMapping(value = "/showAll")
    public JSONResult showAll(Integer page, Integer pageSize)
            throws Exception {

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(page, pageSize);
        return JSONResult.ok(result);
    }
}
