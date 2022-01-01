package com.bootdo.signature.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.bootdo.signature.entity.po.VisitLog;

@Mapper
public interface VisitLogDao {
	@Select("select count(*) from visit_log where app_id = #{appId} and time >= #{start}")
	Long selectPVCountByAppId(@Param("appId")Long appId,@Param("start")Long start);
	
	@Select("select count(DISTINCT session_id) from visit_log where app_id = #{appId}  and time >= #{start}")
	Long selectUVCountByAppId(@Param("appId")Long appId,@Param("start")Long start);
	
	@Select("select count(DISTINCT ip) from visit_log where app_id = #{appId}  and time >= #{start}")
	Long selectIPCountByAppId(@Param("appId")Long appId,@Param("start")Long start);
	
	@Insert("insert into visit_log(app_id,ip,session_id,time) values(#{appId},#{ip},#{sessionId},#{time})")
	@Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
	void save(VisitLog log);
}
