package com.bootdo.signature.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.HeavyVolume;

@Mapper
public interface HeavyVolumeDao {
	@Select("select * from heavy_volume where app_id = #{id}")
	HeavyVolume selectByAppId(long id);
	
	@Update("UPDATE `bootdo`.`heavy_volume` " + 
			"SET " + 
			"`enable`=#{enable}, " + 
			"`start_time`=#{startTime}, " + 
			"`intervals`=#{intervals}, " + 
			"`down_num`=#{downNum}, " + 
			"`add_num`=#{addNum} " + 
			"WHERE (`app_id`=#{appId})")
	void update(HeavyVolume appleAccount);

	@Insert("INSERT INTO `bootdo`.`heavy_volume` (`app_id`, `enable`, `start_time`, `intervals`, `down_num`, `add_num`) " + 
			"VALUES (#{appId}, #{enable}, #{startTime}, #{intervals}, #{downNum}, #{addNum})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(HeavyVolume appleAccount);
}
