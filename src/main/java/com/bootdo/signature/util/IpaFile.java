package com.bootdo.signature.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.bootdo.common.utils.png.ConvertHandler;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;

public class IpaFile {
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final String IPA_TMP_DIR_PREFIX = "ipa_temp";
	public static final String APP_DIR_PARENT  = "Payload";
	public static final String APP_DIR_SUFFIX = ".app";
	
	public class InfoPlist{
		private String CFBundleIdentifier;
		private String CFBundleDisplayName;
		private String CFBundleName;
		private String CFBundleVersion;
		private String MinimumOSVersion;
		private String CFBundleExecutable;
		public String getCFBundleExecutable() {
			return CFBundleExecutable;
		}
		public String getCFBundleIdentifier() {
			return CFBundleIdentifier;
		}
		public String getCFBundleDisplayName() {
			return CFBundleDisplayName;
		}
		public String getCFBundleVersion() {
			return CFBundleVersion;
		}
		public String getMinimumOSVersion() {
			return MinimumOSVersion;
		}
		public String getCFBundleName() {
			return CFBundleName;
		}
	}
	
	private File ipa;
	private String outPath;
	private File infoPlist;
	private File appDir;
	private File icon;
	private File exec;
	private InfoPlist info;

	public IpaFile(File ipa) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
		// 解压缩ipa
		this.ipa = ipa;
		this.outPath = new File(TMP_DIR,IPA_TMP_DIR_PREFIX.concat(ipa.getName())).getAbsolutePath();
		ZipUtil.unZipAndGetFileNames(ipa, this.outPath);
		// 获取app文件夹
		findAppDir();
		// 获取info.plist文件
		findInfoPlist();
		// 解析Info.Plist
		this.info = analyzeInfoPlist();
		// 获取Icon
		findIcon();
		// 获取二进制文件
		findExecutable();
	}

	/**
	 * 解析InfoPlist
	 * @return
	 * @throws IOException
	 * @throws PropertyListFormatException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private InfoPlist analyzeInfoPlist() throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException{
		InfoPlist info = new InfoPlist();
		// 第三方jar包提供
		NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(infoPlist);
		// 应用包名
		NSString parameters = (NSString) rootDict.objectForKey("CFBundleIdentifier");
		info.CFBundleIdentifier = parameters==null ? "":parameters.toString();
		// 应用名称
		parameters = (NSString) rootDict.objectForKey("CFBundleName");
		info.CFBundleName = parameters==null ? "":parameters.toString();
		// 应用名称
		parameters = (NSString) rootDict.objectForKey("CFBundleDisplayName");
		info.CFBundleDisplayName = parameters==null ? "":parameters.toString();
		// 应用版本
		parameters = (NSString) rootDict.objectForKey("CFBundleVersion");
		info.CFBundleVersion = parameters==null ? "":parameters.toString();
		// 应用所需IOS最低版本
		parameters = (NSString) rootDict.objectForKey("MinimumOSVersion");
		info.MinimumOSVersion = parameters==null ? "":parameters.toString();
		// 可执行文件
		parameters = (NSString) rootDict.objectForKey("CFBundleExecutable");
		info.CFBundleExecutable = parameters==null ? "":parameters.toString();
		return info;
	}

	/**
	 * 查找app文件夹
	 */
	private void findAppDir() {
		// 文件是否存在
		File parentDir = new File(this.outPath,APP_DIR_PARENT);
		if(parentDir.exists()) {
			File[] listFiles = parentDir.listFiles();
			for (File file : listFiles) {
				if (file.getName().endsWith(APP_DIR_SUFFIX)) {
					this.appDir = file;
					break;
				}
			}
		}
	}
	
	/**
	 * 查找app图标
	 */
	private void findIcon() {
		File[] fileList = this.appDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".png")
						&& pathname.getName().startsWith("AppIcon");
			}
		});
		for (File f : fileList) {
			if (this.icon == null) {
				this.icon = f;
				continue;
			} else {
				if (f.length() > this.icon.length()) {
					this.icon = f;
				}
			}
		}
		if(this.icon != null) {
			String newIconPath = UUID.randomUUID().toString() + ".png";
			File newIconFile = new File(this.icon.getParentFile(),newIconPath);
			if (new ConvertHandler().convertPNGFile(this.icon, newIconFile)) {
				this.icon = newIconFile;
			}
		}
	}
	
	/**
	 * 查找Info.plist
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws ParseException 
	 * @throws PropertyListFormatException 
	 * @throws IOException 
	 */
	private void findInfoPlist() throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
		File[] infoPlist = this.appDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().equals("Info.plist");
			}
		});
		this.infoPlist = infoPlist[0];
	}
	
	/**
	 * 获取执行文件
	 */
	private void findExecutable() {
		this.exec = new File(this.appDir,this.info.CFBundleExecutable);
	}

	/**
	 * 删除临时文件夹
	 */
	public void deleteTempDir() {
		this.appDir.delete();
	}
	
	public File getIpa() {
		return ipa;
	}

	public String getOutPath() {
		return outPath;
	}

	public File getInfoPlist() {
		return infoPlist;
	}

	public File getAppDir() {
		return appDir;
	}

	public File getIcon() {
		return icon;
	}

	public File getExec() {
		return exec;
	}

	public InfoPlist getInfo() {
		return info;
	}
}
