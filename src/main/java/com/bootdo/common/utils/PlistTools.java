package com.bootdo.common.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.XMLPropertyListParser;

public class PlistTools {
	
	public static String createDownloadPlist(String packageUrl,String packageName,String titleName) {
	    //Creating the root object
	    NSDictionary root = new NSDictionary();
	    //Creation of an array of the length 2
	    NSArray items = new NSArray(1);
	    NSDictionary dict = new NSDictionary();
		NSArray assets = new NSArray(1);
		NSDictionary assets_sub = new NSDictionary();
	    NSString url = new NSString(packageUrl);
	    NSString kind = new NSString("software-package");
		assets_sub.put("kind", kind);
		assets_sub.put("url", url);
		assets.setValue(0, assets_sub);
		NSDictionary metadata = new NSDictionary();
	    NSString bundle_identifier = new NSString(packageName);
	    NSString bundle_version = new NSString("1.7.0");
		NSString mkind = new NSString("software");
		NSString title = new NSString(titleName);
		metadata.put("bundle-identifier", bundle_identifier);
		metadata.put("bundle-version", bundle_version);
		metadata.put("kind", mkind);
		metadata.put("title", title);
		dict.put("assets", assets);
		dict.put("metadata", metadata);
	    items.setValue(0, dict);
	    root.put("items", items);
	 	return root.toXMLPropertyList();
	}

	public static String analysisUDID(byte[] bytes) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
	    //传入byte[]
	    NSObject xx = XMLPropertyListParser.parse(bytes);
	    //你要看你的是什么类型，直接强转
	    HashMap dictionary = (HashMap) xx.toJavaObject();
	    //获取节点
	    Object udid = dictionary.get("UDID");
	    return udid.toString();
	}
}
