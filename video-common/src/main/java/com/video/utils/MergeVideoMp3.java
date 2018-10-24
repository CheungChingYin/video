package com.video.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

	private String ffmpegEXE;

	public MergeVideoMp3(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	public void convertor(String videoInputPath, String mp3InputPath, double seconds, String videoOutputPath)
			throws Exception {
		// ffmpeg.exe -i b.mp3 -i a.mp4 -t 8 -y 新的视频.mp4
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);

		command.add("-i");
		command.add(mp3InputPath);		

		command.add("-i");
		command.add(videoInputPath);

		command.add("-t");
//		command.add(String.valueOf(seconds));
		command.add("8");
		command.add("-y");
		command.add(videoOutputPath);

//		for (String c : command) {
//			System.out.print(c + " ");
//		}

		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();

		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);

		String line = "";
		System.out.println("合成新视频："+videoOutputPath);
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

	public static void main(String[] args) {
		MergeVideoMp3 ffmpeg = new MergeVideoMp3("F:\\soft\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("F:\\soft\\ffmpeg\\bin\\little.mp4", "F:\\soft\\ffmpeg\\bin\\Despacito 林俊杰 + LuisFonsi.mp3", 8,
					"F:\\soft\\ffmpeg\\bin\\av3.mp4");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
