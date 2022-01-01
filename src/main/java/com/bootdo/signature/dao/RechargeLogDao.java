package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.bootdo.signature.entity.po.RechargeLog;

@Mapper
public interface RechargeLogDao {
	@Select("select sum(number) from recharge_log where user_id = #{userId}")
	Long selectCountByUserId(@Param("userId")Long userId);
	
	@Select("select sum(number) from recharge_log")
	Long selectCount();
	
	@Insert("insert into recharge_log(user_id,number,create_time,creater_id,creater_name,user_name) values(#{userId},#{number},#{createTime},#{createrId},#{createrName},#{userName})")
	@Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
	void save(RechargeLog log);

	@Select("select * from recharge_log where user_id = #{userId} order by create_time desc limit #{start},#{item}")
	List<RechargeLog> selectListLimitByUserId(int start,int item,Long userId);
	
	@Select("select * from recharge_log order by create_time desc limit #{start},#{item}")
	List<RechargeLog> selectListLimit(int start,int item);
}
