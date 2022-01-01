package com.bootdo.system.shiro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bootdo.common.config.ApplicationContextRegister;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.system.dao.UserDao;
import com.bootdo.system.domain.RoleDO;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.MenuService;
import com.bootdo.system.service.RoleService;

public class UserRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		Long userId = ShiroUtils.getUserId();
		MenuService menuService = ApplicationContextRegister.getBean(MenuService.class);
		Set<String> perms = menuService.listPerms(userId);
		RoleService roleService = ApplicationContextRegister.getBean(RoleService.class);
		List<RoleDO> roleList = roleService.list(userId);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(perms);
		roleList.forEach(item->{
			if("true".equals(item.getRoleSign())) {
				info.addRole(roleService.get(item.getRoleId()).getRoleSign());
			}
		});
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token){
		String username = (String) token.getPrincipal();
		Map<String, Object> map = new HashMap<>(16);
		map.put("username", username);
		String password = new String((char[]) token.getCredentials());

		UserDao userMapper = ApplicationContextRegister.getBean(UserDao.class);
		// 查询用户信息
		List<UserDO> userList = userMapper.list(map);

		// 账号不存在
		if (userList.isEmpty()) {
			throw new UnknownAccountException("账号或密码不正确");
		}

		UserDO user = userList.get(0);
		
		// 密码错误
		if (!password.equals(user.getPassword())) {
			throw new IncorrectCredentialsException("账号或密码不正确");
		}

		// 账号锁定
		if (user.getStatus() == 0) {
			throw new LockedAccountException("账号已被锁定,请联系管理员");
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
		return info;
	}

}
