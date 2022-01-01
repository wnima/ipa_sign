package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.AndroidDownload;

@Mapper
public interface AndroidDownloadDao {
	// 插入
	@Insert("insert into android_download(id,count) values(#{id},#{count})")
	void insert(AndroidDownload down);
	// 更新(次数)
	@Update("update android_download set count=count+1 where id=#{id}")
	void countIncrease(int id);
	// 更新(次数)
	@Select("select * from android_download where id=#{id}")
	List<AndroidDownload> select(long id);
}
