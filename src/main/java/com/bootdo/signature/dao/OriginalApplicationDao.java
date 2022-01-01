package com.bootdo.signature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bootdo.signature.entity.po.OriginalApplication;

@Mapper
public interface OriginalApplicationDao {
	@Select("select * from original_application where app_id = #{appId}")
	@Results({
		@Result(id = true,property = "appId",column = "app_id"),
		@Result(property = "packageName",column = "package_name"),
		@Result(property = "applicationName",column = "application_name"),
		@Result(property = "filePath",column = "file_path"),
		@Result(property = "iconUrl",column = "icon_url"),
		@Result(property = "userId",column = "user_id"),
		@Result(property = "push",column = "push"),
		@Result(property = "appType",column = "app_type"),
		@Result(property = "fileUrl",column = "file_url"),
		@Result(property = "udidTitle",column = "udid_title"),
		@Result(property = "udidDescription",column = "udid_description"),
		@Result(property = "downloadNum",column = "download_num"),
		@Result(property = "androidFileUrl",column = "android_file_url"),
		@Result(property = "instartModel",column = "instart_model"),
		@Result(property = "instartPassword",column = "instart_password"),
		@Result(property = "realDownNum",column = "real_down_num"),
		@Result(property = "area",column = "area"),
		@Result(property = "downloadCode",column = "download_code"),
	})
	OriginalApplication selectById(@Param("appId")Long appId);

	@Insert("INSERT INTO original_application "
			+ "("
			+ "package_name,"
			+ "organization_name,"
			+ "application_name,"
			+ "file_path,"
			+ "icon_url,"
			+ "file_size,"
			+ "version,"
			+ "enable,"
			+ "type,"
			+ "mark,"
			+ "shop,"
			+ "introduce,"
			+ "user_id,"
			+ "create_time,"
			+ "min_version,"
			+ "remark_name,"
			+ "show_app_name,"
			+ "short_domain,"
			+ "screenshots,"
			+ "show_icon_url,"
			+ "push,"
			+ "app_type,"
			+ "file_url,"
			+ "udid_title,"
			+ "udid_description,"
			+ "android_file_url,"
			+ "instart_model,"
			+ "instart_password,"
			+ "real_down_num,"
			+ "area,"
			+ "download_code,"
			+ "download_num"
			+ ")"
			+ "VALUES "
			+ "("
			+ "#{packageName},"
			+ "#{organizationName},"
			+ "#{applicationName},"
			+ "#{filePath},"
			+ "#{iconUrl},"
			+ "#{fileSize},"
			+ "#{version},"
			+ "#{enable},"
			+ "#{type},"
			+ "#{mark},"
			+ "#{shop},"
			+ "#{introduce},"
			+ "#{userId},"
			+ "#{createTime},"
			+ "#{minVersion},"
			+ "#{remarkName},"
			+ "#{showAppName},"
			+ "#{shortDomain},"
			+ "#{screenshots},"
			+ "#{showIconUrl},"
			+ "#{push},"
			+ "#{appType},"
			+ "#{fileUrl},"
			+ "#{udidTitle},"
			+ "#{udidDescription},"
			+ "#{androidFileUrl},"
			+ "#{instartModel},"
			+ "#{instartPassword},"
			+ "#{realDownNum},"
			+ "#{area},"
			+ "#{downloadCode},"
			+ "#{downloadNum}"
			+ ")")
	@Options(useGeneratedKeys=true, keyProperty="appId", keyColumn="app_id")
	void save(OriginalApplication app);

	@Update("update original_application "
			+ "set "
			+ "package_name = #{app.packageName},"
			+ "organization_name=#{app.organizationName},"
			+ "application_name=#{app.applicationName},"
			+ "file_path=#{app.filePath},"
			+ "icon_url=#{app.iconUrl},"
			+ "file_size=#{app.fileSize},"
			+ "version=#{app.version},"
			+ "enable=#{app.enable},"
			+ "type=#{app.type},"
			+ "mark=#{app.mark},"
			+ "shop=#{app.shop},"
			+ "introduce=#{app.introduce},"
			+ "user_id=#{app.userId},"
			+ "create_time=#{app.createTime},"
			+ "min_version=#{app.minVersion},"
			+ "remark_name=#{app.remarkName},"
			+ "show_app_name=#{app.showAppName},"
			+ "short_domain=#{app.shortDomain},"
			+ "screenshots=#{app.screenshots},"
			+ "show_icon_url=#{app.showIconUrl},"
			+ "push=#{app.push},"
			+ "app_type=#{app.appType},"
			+ "file_url=#{app.fileUrl},"
			+ "udid_title=#{app.udidTitle},"
			+ "udid_description=#{app.udidDescription},"
			+ "android_file_url=#{app.androidFileUrl},"
			+ "instart_model=#{app.instartModel},"
			+ "instart_password=#{app.instartPassword},"
			+ "real_down_num= real_down_num + #{realAddNum},"
			+ "area=#{app.area},"
			+ "download_num= download_num + #{addNum} "
			+ "where "
			+ "app_id = #{app.appId}")
	void update(@Param("app")OriginalApplication app,@Param("addNum")int addNum,@Param("realAddNum")int realAddNum);
	
	@Select("select * from original_application where user_id = #{userId} and application_name like #{search} limit #{start},#{num}")
	@Results({
		@Result(id = true,property = "appId",column = "app_id"),
		@Result(property = "packageName",column = "package_name"),
		@Result(property = "applicationName",column = "application_name"),
		@Result(property = "filePath",column = "file_path"),
		@Result(property = "iconUrl",column = "icon_url"),
		@Result(property = "userId",column = "user_id"),
		@Result(property = "push",column = "push"),
		@Result(property = "appType",column = "app_type"),
		@Result(property = "fileUrl",column = "file_url"),
		@Result(property = "udidTitle",column = "udid_title"),
		@Result(property = "udidDescription",column = "udid_description"),
		@Result(property = "downloadNum",column = "download_num"),
		@Result(property = "androidFileUrl",column = "android_file_url"),
		@Result(property = "instartModel",column = "instart_model"),
		@Result(property = "instartPassword",column = "instart_password"),
		@Result(property = "area",column = "area"),
		@Result(property = "downloadCode",column = "download_code"),
	})
	List<OriginalApplication> selectByOwnerId(@Param("userId")Long userId, @Param("start")int start, @Param("num")Integer num, @Param("search")String search);

	@Delete("delete from original_application where app_id = #{appid}")
	int delete(Long appId);

	@Select("select * from original_application where short_domain = #{shortDomain}")
	@Results({
		@Result(id = true,property = "appId",column = "app_id"),
		@Result(property = "packageName",column = "package_name"),
		@Result(property = "applicationName",column = "application_name"),
		@Result(property = "filePath",column = "file_path"),
		@Result(property = "iconUrl",column = "icon_url"),
		@Result(property = "userId",column = "user_id"),
		@Result(property = "push",column = "push"),
		@Result(property = "appType",column = "app_type"),
		@Result(property = "fileUrl",column = "file_url"),
		@Result(property = "udidTitle",column = "udid_title"),
		@Result(property = "udidDescription",column = "udid_description"),
		@Result(property = "downloadNum",column = "download_num"),
		@Result(property = "androidFileUrl",column = "android_file_url"),
		@Result(property = "instartModel",column = "instart_model"),
		@Result(property = "instartPassword",column = "instart_password"),
		@Result(property = "area",column = "area"),
		@Result(property = "downloadCode",column = "download_code"),
	})
	OriginalApplication selectByShortDomain(@Param("shortDomain")String turl);

	@Select("select * from original_application where application_name like #{search} limit #{start},#{num}")
	@Results({
		@Result(id = true,property = "appId",column = "app_id"),
		@Result(property = "packageName",column = "package_name"),
		@Result(property = "applicationName",column = "application_name"),
		@Result(property = "filePath",column = "file_path"),
		@Result(property = "iconUrl",column = "icon_url"),
		@Result(property = "userId",column = "user_id"),
		@Result(property = "push",column = "push"),
		@Result(property = "appType",column = "app_type"),
		@Result(property = "fileUrl",column = "file_url"),
		@Result(property = "udidTitle",column = "udid_title"),
		@Result(property = "udidDescription",column = "udid_description"),
		@Result(property = "downloadNum",column = "download_num"),
		@Result(property = "androidFileUrl",column = "android_file_url"),
		@Result(property = "instartModel",column = "instart_model"),
		@Result(property = "instartPassword",column = "instart_password"),
		@Result(property = "area",column = "area"),
		@Result(property = "downloadCode",column = "download_code"),
	})
	List<OriginalApplication> selectLimit(@Param("start")int start, @Param("num")int num,@Param("search")String search);

	@Select("select count(*) from original_application")
	long count();

	@Select("select count(*) from original_application where user_id = #{userId}")
	long selectCountByOwnerId(Long userId);

	@Select("select * from original_application where download_code = #{code}")
	OriginalApplication selectByDownloadCode(String code);
}
