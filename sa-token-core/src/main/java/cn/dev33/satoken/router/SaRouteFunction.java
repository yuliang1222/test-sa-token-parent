package cn.dev33.satoken.router;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;

public interface SaRouteFunction {
	/**
	 * 执行验证的方法
	 * @param request
	 * @param response
	 * @param handler
	 */
	public void run(SaRequest request, SaResponse response, Object handler);
}
