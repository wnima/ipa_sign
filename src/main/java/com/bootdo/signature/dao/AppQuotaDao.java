package com.bootdo.signature.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.AppQuota;

@Mapper
public interface AppQuotaDao {
	// 根据应用ID查配额
	@Select("select * from app_quota where app_id = #{appId}")
	public AppQuota selectByAppId(long appId);
	// 更新配额限制
	@Update("update app_quota set max_num=#{limit} where id = #{id}")
	public int updateLimitById(@Param("limit")int limit,@Param("id")int id);
	// 更新配额数量
	@Update("update app_quota set used_num=#{num} where id = #{id}")
	public int updateNumById(@Param("num")int num,@Param("id")int id);
	// 插入配额
	@Insert("insert into app_quota(app_id,max_num,used_num) values(#{appId},#{maxNum},#{usedNum})")
	public int insert(AppQuota app);
	// 删除配额
	@Delete("delete from app_quota where id=#{id}")
	public int deleteById(int id);
}
