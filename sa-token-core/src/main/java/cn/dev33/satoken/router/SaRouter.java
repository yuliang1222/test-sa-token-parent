package cn.dev33.satoken.router;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;

import java.util.List;

/**
 * 路由匹配操作工具类
 */
public class SaRouter {

	public static boolean isMatch(String path,String... patterns) {
		if (patterns == null) {
			return false;
		}
		for (int i = 0; i < patterns.length; i++) {
			if (SaManager.getSaTokenContextOrSecond().matchPath(patterns[i], path)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMatch(SaHttpMethod[] methods, String methodString) {
		if (methods == null) {
		    return false;
		}
		for (SaHttpMethod method : methods) {
			if (method == SaHttpMethod.ALL || (method != null && method.toString().equalsIgnoreCase(methodString))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMatchCurrURI(String... pattern){
		return isMatch(SaHolder.getRequest().getRequestPath(),pattern);
	}

	public static boolean isMatchCurrMethod(SaHttpMethod[] methods) {
		return isMatch(methods, SaHolder.getRequest().getMethod());
	}




}
