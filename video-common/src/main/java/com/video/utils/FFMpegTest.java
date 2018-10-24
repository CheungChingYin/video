package com.video.utils;

import org.apache.tomcat.jni.Proc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String videoOutputPath) throws Exception {
        //转换视频格式命令：fmpeg -i input.mp4 output.avi
        //设置命令格式
        List<String> command = new ArrayList<>();
        //添加命令
        command.add(ffmpegEXE);
        //添加命令参数
        command.add("-i");
        command.add(videoInputPath);
        command.add("-y");
        command.add(videoOutputPath);

        for (String c : command) {
            System.out.print(c + " ");
        }
        //java调用CMD命令
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        //读取错误流，即可释放
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public void mergMp3Mp4(String bgmInputPath, String videoInputPath, String videoOutputPath) throws IOException {
        List<String> command = new ArrayList<String>();
        //添加命令
        command.add(ffmpegEXE);
        //添加命令参数
        command.add("-i");
        command.add(bgmInputPath);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-t");
        command.add("7");
        command.add("-Y");
        command.add(videoOutputPath);

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();


    }

    public static void main(String[] args) {
        FFMpegTest ffmpeg = new FFMpegTest("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
//			ffmpeg.convertor("F:\\soft\\ffmpeg\\bin\\a.mp4", "F:\\soft\\ffmpeg\\bin\\b.avi");
            ffmpeg.mergMp3Mp4("E:\\ffmpeg\\bin\\MKJTime.mp3", "E:\\ffmpeg\\bin\\input.mp4", "E:\\ffmpeg\\bin\\output.mp4");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
