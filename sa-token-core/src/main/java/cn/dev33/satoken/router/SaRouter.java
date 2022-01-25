package cn.dev33.satoken.router;

import cn.dev33.satoken.SaManager;

import java.util.List;

/**
 * 路由匹配操作工具类
 */
public class SaRouter {
	public static boolean isMatch(String pattern, String path) {
		return SaManager.getSaTokenContextOrSecond().matchPath(pattern, path);
	}
	public static boolean isMatch(List<String> patterns, String path) {
		if (patterns == null) {
			return false;
		}
		for (String pattern : patterns) {
			if (isMatch(pattern, path)) {
				return true;
			}
		}
		return false;
	}









}
