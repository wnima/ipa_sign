package com.bootdo.signature.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.bootdo.signature.entity.po.AndroidDownloadLog;

@Mapper
public interface AndroidDownloadLogDao {
	@Insert("insert into android_download_log(app_id,ip,detail) values(#{appId},#{ip},#{detail})")
	void insert(AndroidDownloadLog log);
}
