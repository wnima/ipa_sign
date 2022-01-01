package com.bootdo.signature.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	public static void uploadFile(MultipartFile file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		//file.transferTo(new File(filePath + fileName));
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		BufferedInputStream in = new BufferedInputStream(file.getInputStream());
		int len = 0;
		byte[] b = new byte[2048];
		while((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.flush();
		out.close();
		in.close();
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static String renameToUUID(String fileName) {
		return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public static void uploadFile(byte[] b, String uploadPath, String fileName) throws IOException {
		File targetFile = new File(uploadPath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		System.out.println(targetFile.getAbsolutePath());
		FileOutputStream out = new FileOutputStream(uploadPath + fileName);
		out.write(b);
		out.flush();
		out.close();
	}
	
	public static void saveToFile(MultipartFile file,File newFile) throws IOException {
		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = FileUtils.openOutputStream(newFile);
			in = file.getInputStream();
			int len = 0;
			byte[] temp = new byte[2048];
			while ((len = in.read(temp)) != -1) {
				out.write(temp, 0, len);
			}
		}catch (IOException e) {
			throw e;
		}finally {
			if(out != null) {
				out.close();
				out = null;
			}
		}
	}
}
