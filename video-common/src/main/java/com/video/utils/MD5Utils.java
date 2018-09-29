package com.video.utils;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * <p>
 * Title: MD5Utils
 * </p>
 * 
 * <p>
 * Description: 对密码进行MD5加密
 * </p>
 * 
 * @author JXPeng
 * 
 * @date 2018年9月17日
 * 
 */
public class MD5Utils {

	/**
	 * @Description: 对字符串进行md5加密
	 */
	public static String getMD5Str(String strValue) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
		return newstr;
	}

	public static void main(String[] args) {
		try {
			String md5 = getMD5Str("imooc");
			System.out.println(md5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
