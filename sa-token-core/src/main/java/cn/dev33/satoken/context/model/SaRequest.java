package cn.dev33.satoken.context.model;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Request 包装类
 */
public interface SaRequest {
	public Object getSource();

	public String getParam(String name);

	public default String getParam(String name, String defaultValue) {
		String value = getParam(name);
		if (SaFoxUtil.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * /**
	 * 	 * 检测提供的参数是否为指定值
	 * @param name
	 * @param value
	 * @return
	 */
	public default boolean isParam(String name, String value) {
		String paramValue = getParam(name);
		return SaFoxUtil.isNotEmpty(paramValue) && paramValue.equals(value);
	}

	public default boolean hasParam(String name) {
		return SaFoxUtil.isNotEmpty(getParam(name));
	}

	public default String getParamNotNull(String name) {
		String paramValue = getParam(name);
		if (SaFoxUtil.isEmpty(paramValue)) {
			throw new SaTokenException("缺少参数: " + name);
		}
		return paramValue;
	}
	public String getHeader(String name);
	public default String getHeader(String name, String defaultValue) {
		String value = getHeader(name);
		if (SaFoxUtil.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}
	public String getCookieValue(String name);
	public String getRequestPath();
	public default boolean isPath(String path) {
		return getRequestPath().equals(path);
	}
	public String getUrl();
	public String getMethod();
	public default boolean isAjax() {
		return getHeader("X-Requested-With") != null;
	}
	public default Object forward(String path) {
		throw new SaTokenException("No implementation");
	}

}
