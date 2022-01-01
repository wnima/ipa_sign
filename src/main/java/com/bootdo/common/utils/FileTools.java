package com.bootdo.common.utils;

import java.io.File;

public class FileTools {
	
	public static String checkFileParentPath(File file) {
		if(!file.exists()) {
			File parent = file.getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}
		}
		return file.getAbsolutePath();
	}
	
	public static String checkFileParentPath(String filePath) {
		File file =  new File(filePath);
		return checkFileParentPath(file);
	}
	
	public static String checkDirectoryPath(File file) {
		if(!file.exists()) {
			file.mkdirs();
		}else if(!file.isDirectory()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static String checkDirectoryPath(String filePath) {
		File file =  new File(filePath);
		return checkDirectoryPath(file);
	}
}
