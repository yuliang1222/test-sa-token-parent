package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文处理器
 * @author kong
 *
 */
public interface SaTokenContext {
	public SaRequest getRequest();
	public SaResponse getResponse();
	public SaStorage getStorage();
	public boolean matchPath(String pattern, String path);

	public default boolean isValid() {
		return false;
	}
}
