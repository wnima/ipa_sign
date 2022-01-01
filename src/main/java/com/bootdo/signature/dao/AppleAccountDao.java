package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.AppleAccount;

@Mapper
public interface AppleAccountDao {
	@Select("select * from apple_account")
	@Results({
		@Result(column = "apackage_name",property = "apackageName"),
		@Result(column = "register_number",property = "registerNumber"),
	})
	List<AppleAccount> selectAccountList();
	
	@Select("select * from apple_account where id = #{id}")
	@Results({
		@Result(column = "package_name",property = "packageName"),
		@Result(column = "register_number",property = "registerNumber"),
		
	})
	AppleAccount selectAccountById(long id);
	
	@Select("select * from apple_account where username = #{username}")
	@Results({
		@Result(column = "package_name",property = "packageName"),
		@Result(column = "register_number",property = "registerNumber"),
		
	})
	AppleAccount selectAccountByUsername(String username);
	
	//@Update("update apple_account set register_number=#{registerNumber},description_file=#{descriptionFile},enable=#{enable} where id=#{id}")
	@Update("	UPDATE `bootdo`.`apple_account` " + 
			"	SET " + 
			"	`username`=#{username}, " + 
			"	`password`=#{password}, " + 
			"	`certificate`=#{certificate}, " + 
			"	`key_of_certificate`=#{keyOfCertificate}, " + 
			"	`description_file`=#{descriptionFile}, " + 
			"	`register_number`=#{registerNumber}, " + 
			"	`package_name`=#{packageName}, " + 
			"	`enable`=#{enable}, " + 
			"	`blockade`=#{blockade}, " + 
			"	`loginstate`=#{loginstate}," + 
			"	`p8`=#{p8}," + 
			"	`iss`=#{iss}," + 
			"	`bundle_id`=#{bundleId}," + 
			"	`cer_id`=#{cerId}," + 
			"	`cer_type`=#{cerType}," + 
			"	`use`=#{use}," + 
			"	`kid`=#{kid}" + 
			"	WHERE (`id`=#{id})")
	void updateAccount(AppleAccount appleAccount);

	@Insert("	INSERT INTO `bootdo`.`apple_account` (`username`, `password`,`phone_number`, `certificate`, `key_of_certificate`, `description_file`, `register_number`, `package_name`, `enable`, `blockade`, `loginstate`,p8,iss,kid,bundle_id,cer_id,cer_type) " + 
			"	VALUES (#{username}, #{password},#{phoneNumber}, #{certificate}, #{keyOfCertificate}, #{descriptionFile}, #{registerNumber}, #{packageName}, #{enable}, #{blockade}, #{loginstate}, #{p8}, #{iss}, #{kid}, #{bundleId},#{cerId},#{cerType})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(AppleAccount appleAccount);
}
