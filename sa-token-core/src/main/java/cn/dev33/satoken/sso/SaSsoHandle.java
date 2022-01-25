package cn.dev33.satoken.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;

public class SaSsoHandle {

	public static Object serverRequest() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		return null;
	}
}
