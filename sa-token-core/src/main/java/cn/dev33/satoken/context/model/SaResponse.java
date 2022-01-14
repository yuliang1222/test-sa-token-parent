package cn.dev33.satoken.context.model;

public interface SaResponse {
	public Object getSource();
	public default void deleteCookie(String name){
		addCookie(name, null, null, null, 0);
	}

	public default void addCookie(String name, String value, String path, String domain, int timeout) {
		this.addCookie(new SaCookie(name,value).setPath(path).setDomain(domain).setMaxAge(timeout));
	}
	public default void addCookie(SaCookie cookie) {
		this.addHeader(SaCookie.HEADER_NAME, cookie.toHeaderValue());
	}
	public SaResponse setStatus(int sc);

	public SaResponse setHeader(String name, String value);

	public SaResponse addHeader(String name, String value);

	public default SaResponse setServer(String value) {
		return this.setHeader("Server", value);
	}

	public Object redirect(String url);
}
