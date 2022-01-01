package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.EnterprisePackage;

@Mapper
public interface EnterprisePackageDao {
	@Select("select count(*) from enterprise_package")
	int selectCount();
	
	@Select("select * from enterprise_package where app_id=#{appId}")
	EnterprisePackage selectByAppId(long appId);
	
	@Select("select * from enterprise_package limit #{offset},#{num}")
	List<EnterprisePackage> selectLimit(int offset,int num);
	
	@Insert("insert into enterprise_package(app_id,url,status) values(#{appId},#{url},#{status})")
	int insert(EnterprisePackage enterprisePackage);
	
	@Update("update enterprise_package set app_id=#{appId},url=#{url},status=#{status} where id=#{id}")
	int update(EnterprisePackage enterprisePackage);
	
	@Delete("delete from enterprise_package where id = #{id}")
	int deleteByid(int id);
}
