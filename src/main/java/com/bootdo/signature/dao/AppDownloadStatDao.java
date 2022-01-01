package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.bootdo.signature.entity.po.AppDownloadStat;

@Mapper
public interface AppDownloadStatDao {
	String QUERY_DOWNLOAD_LOG = "select count(distinct udid) from download_log where app_id=#{appId} and download_time >= #{startTime} and download_time < #{endTime} ";
	String QUERY_HOUR_RECORD = "select * from app_download_stat_hour where app_id=#{appId} order by t_time desc limit #{offset},#{item}";
	String QUERY_DAY_RECORD = "select * from app_download_stat_day where app_id=#{appId} order by t_time desc limit #{offset},#{item}";
	String QUERY_MONTH_RECORD = "select * from app_download_stat_month where app_id=#{appId} order by t_time desc limit #{offset},#{item}";
	
	@Select(QUERY_DOWNLOAD_LOG)
	int queryDownloadCountByAppId(@Param("appId")int appId,@Param("startTime")long startTime,@Param("endTime")long endTime);

	@Select(QUERY_HOUR_RECORD)
	List<AppDownloadStat> queryHourRecord(@Param("appId")int appId,@Param("offset")int offset,@Param("item")int item);
	
	@Select(QUERY_DAY_RECORD)
	List<AppDownloadStat> queryDayRecord(@Param("appId")int appId,@Param("offset")int offset,@Param("item")int item);
	
	@Select(QUERY_MONTH_RECORD)
	List<AppDownloadStat> queryMonthRecord(@Param("appId")int appId,@Param("offset")int offset,@Param("item")int item);
}
