package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.UniqueIdentificationLog;

@Mapper
public interface UniqueIdentificationLogDao {
	@Select("select * from unique_identification_log where udid = #{udid}")	
	@Results({
		@Result(column = "udid",property = "udid"),
		@Result(column = "account_id",property = "accountId"),
		@Result(column = "mobileprovision",property = "mobileprovision"),
	})
	List<UniqueIdentificationLog> selectByUdid(@Param("udid")String udid);
	
	@Insert("insert into unique_identification_log(account_id,udid,mobileprovision) values(#{accountId},#{udid},#{mobileprovision})")
	void insert(UniqueIdentificationLog uniq);

	@Delete("DELETE FROM unique_identification_log WHERE udid=#{udid}")
	void delete(String udid);

	@Update("update unique_identification_log set account_id=#{accountId}, mobileprovision=#{mobileprovision} where udid=#{udid}")
	void update(UniqueIdentificationLog udidEntrty);
}
