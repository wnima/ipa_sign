package com.bootdo.signature.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.Recharge;

@Mapper
public interface RechargeDao {
	@Select("select * from recharge where user_id = #{userId}")
	Recharge selectByUserId(@Param("userId")Long userId);
	
	@Insert("insert into recharge(user_id,recharge_num,use_num) values(#{userId},#{rechargeNum},#{useNum})")
	@Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
	void insert(Recharge recharge);
	
	@Update("update recharge set recharge_num = #{rechargeNum}, use_num = #{useNum} where user_id = #{userId}")
	int update(Recharge recharge);
}
