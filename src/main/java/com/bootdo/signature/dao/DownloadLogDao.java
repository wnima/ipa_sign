package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.DownloadLog;

@Mapper
public interface DownloadLogDao {
	@Select("select * from download_log where user_id = #{userId}")
	List<DownloadLog> selectByUserId(@Param("userId")long userId);
	
	@Select("select (@n:=@n+1) no,log.id,log.download_time,log.udid,log.app_id,log.isreal from download_log log,(select @n:=0) no where user_id = #{userId} GROUP BY log.udid ORDER BY id limit #{start},#{item}")
	List<DownloadLog> limitSelectByUserIdAndDistinct(@Param("userId")long userId,@Param("start")int start,@Param("item")int item);
	
	@Select("select (@n:=@n+1) no,log.id,log.download_time,log.udid,log.app_id,log.isreal from download_log log,(select @n:=0) no where user_id = #{userId} ORDER BY id limit #{start},#{end}")
	List<DownloadLog> limitSelectByUserId(@Param("userId")long userId,@Param("start")int start,@Param("end")int end);
	
	@Select("select count(*) from download_log where user_id = #{userId}")
	Long selectCountByUserId(@Param("userId")long userId);

	@Select("select count(distinct udid) from download_log where user_id = #{userId}")
	Long selectCountByUserIdAndDistinctUdid(@Param("userId")long userId);

	
	@Insert("INSERT INTO download_log (download_time, udid, app_id, user_id, isreal) VALUES(#{downloadTime},#{udid},#{appId},#{userId},#{isreal})")
	@Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
	void save(DownloadLog log);

	@Select("select * from download_log where user_id = #{userId} and download_time >= ${start} and download_time < ${end}")
	List<DownloadLog> selectCountByUserIdAndTime(@Param("userId")long userId, @Param("start")long start, @Param("end")long end);

	@Select("select count(distinct udid) from download_log where user_id = #{userId} and download_time > #{start}")
	Long selectCountByUserIdAndDistinctUdidAndTime(@Param("userId")long userId, @Param("start")long timeInMillis);

	@Select("select count(*) from download_log where app_id = #{appId}")
	Long selectCountByAppId(long appId);

	@Update("UPDATE download_log SET udid=#{newUdid} WHERE udid = #{oldUdid}")
	void changeLogs(@Param("oldUdid")String oldUdid,@Param("newUdid")String newUdid);

	@Select("select count(distinct udid) from download_log where app_id = #{appId}")
	Long selectCountByAppIdAndDistinctUdid(Long appId);
	
	@Select("select count(distinct udid) from download_log where app_id = #{appId} and isreal = #{isreal} and download_time >= #{startTime} and download_time <= #{endTime}")
	Long selectCountByAppIdAndDistinctUdidAndReal(@Param("appId")Long appId,@Param("isreal")int real,@Param("startTime")long startTime,@Param("endTime")long endTime);
}
