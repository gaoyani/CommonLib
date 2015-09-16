package com.huiwei.commonlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class FileManager {

	public FileManager() {
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
		} else {
			sdDir = Environment.getRootDirectory().getAbsoluteFile();
		}

		return sdDir.toString();
	}

	public static String createDir(String dir) {
		String targetDir = getSDPath() + dir;
		File file = new File(targetDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		return targetDir;
	}

	// write file
	public static void writeTxtFile(String dir, String fileName,
			String write_str) throws IOException {
		String targetDir = createDir(dir);

		try {
			FileOutputStream fout = new FileOutputStream(targetDir + fileName);
			byte[] bytes = write_str.getBytes();

			fout.write(bytes);
			fout.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// read file
	public static String readTxtFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean saveBitmap(String fileName, Bitmap bitmap,
			CompressFormat format) {
		if (fileName.length() == 0)
			return false;

		try {
			Log.d("FileManager", fileName);
			FileOutputStream out = new FileOutputStream(fileName);  
			bitmap.compress(format, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
