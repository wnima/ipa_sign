package com.bootdo.signature.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ZipUtil {
	public static int BUFFER_SIZE = 2048;

	public static List<String> unZipAndGetFileNames(File zipfile, String destDir) throws IOException {
		if (StringUtils.isBlank(destDir)) {
			destDir = zipfile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		ZipArchiveInputStream is = null;
		List<String> fileNames = new ArrayList<String>();

		File destDirFile = new File(destDir);
		if (!destDirFile.exists()) {
			destDirFile.mkdirs();
		}

		try {
			is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipfile), BUFFER_SIZE));
			ZipArchiveEntry entry = null;
			while ((entry = is.getNextZipEntry()) != null) {
				fileNames.add(entry.getName());
				if (entry.isDirectory()) {
					File directory = new File(destDir, entry.getName());
					directory.mkdirs();
				} else {
					OutputStream os = null;
					try {
						File file = new File(destDir, entry.getName());
						File parentFile = file.getParentFile();
						if (!parentFile.exists()) {
							parentFile.mkdirs();
						}
						os = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
						IOUtils.copy(is, os);
					}catch (IOException e) {
						throw e;
					}finally {
						IOUtils.closeQuietly(os);
					}
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
		}
		return fileNames;
	}
}
