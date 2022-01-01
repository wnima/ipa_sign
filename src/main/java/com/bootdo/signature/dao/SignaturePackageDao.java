package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.bootdo.signature.entity.po.SignaturePackage;

@Mapper
public interface SignaturePackageDao {
	@Select("select * from signature_package where udid = #{udid} and app_id = #{appId}")
	@Results({
		@Result(column = "file_url",property = "fileUrl")
	})
	List<SignaturePackage> selectSignaturePackageByPackageNameAndUdid(@Param("udid")String udid,@Param("appId")Long appId);
	
	@Insert("insert into signature_package(app_id,file_url,udid) values(#{appId},#{fileUrl},#{udid})")
	void insertSignaturePackage(@Param("udid")String udid,@Param("appId")Long appId,@Param("fileUrl")String fileUrl);

	@Delete("delete from signature_package where udid=#{udid}")
	void deleteByUdid(String udid);
	
	@Delete("delete from signature_package where app_id=#{appid}")
	void deleteByAppId(long appid);
}
