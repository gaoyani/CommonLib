package com.huiwei.commonlib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public  class MD5 {
	public static String getRandomString(int length) { // length��ʾ����ַ�ĳ���
		String base = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLOMNOPQRSTUVWXYZ"; // ����ַ�Ӵ�������ȡ
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	public static String md5s(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer(""); 
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			System.out.println("result: " + buf.toString());// 32λ�ļ���
			System.out.println("result: " + buf.toString().substring(8, 24));// 16λ�ļ���
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;	
		}
	}
}

	 