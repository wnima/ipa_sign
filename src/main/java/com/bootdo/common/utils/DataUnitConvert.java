package com.bootdo.common.utils;

public class DataUnitConvert {
	public static String carry(long size) {
		float resusize = size;
		String unit = "b";
		float remainder = 0;
		while(!(resusize < 1024)) {
			resusize = size / 1024;
			remainder = size % 1024;
		}
		return null;
	}
}
