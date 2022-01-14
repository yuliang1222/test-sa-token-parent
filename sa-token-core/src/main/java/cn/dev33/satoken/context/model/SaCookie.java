package cn.dev33.satoken.context.model;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;

import javax.management.loading.PrivateMLet;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SaCookie {
	public static final String HEADER_NAME = "Set-Cookie";
	private String name;
	private String value;
	private int maxAge = -1;
	private String domain;
	private String path;
	private Boolean secure = false;
	private Boolean httpOnly = false;
	private String sameSite;

	public SaCookie() {
	}

	public SaCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 名称
	 * @return 对象自身
	 */
	public SaCookie setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public SaCookie setValue(String value) {
		this.value = value;
		return this;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public SaCookie setMaxAge(int maxAge) {
		this.maxAge = maxAge;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public SaCookie setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public String getPath() {
		return path;
	}

	public SaCookie setPath(String path) {
		this.path = path;
		return this;
	}

	public Boolean getSecure() {
		return secure;
	}

	public SaCookie setSecure(Boolean secure) {
		this.secure = secure;
		return this;
	}

	public Boolean getHttpOnly() {
		return httpOnly;
	}

	public SaCookie setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	public String getSameSite() {
		return sameSite;
	}

	public SaCookie setSameSite(String sameSite) {
		this.sameSite = sameSite;
		return this;
	}

	@Override
	public String toString() {
		return "SaCookie [name=" + name + ", value=" + value + ", maxAge=" + maxAge + ", domain=" + domain + ", path=" + path
				+ ", secure=" + secure + ", httpOnly=" + httpOnly + ", sameSite="
				+ sameSite + "]";
	}

	public void builde() {
		if (path == null) {
			path = "/";
		}
	}

	public String toHeaderValue() {
		this.builde();
		if (SaFoxUtil.isEmpty(name)) {
			throw new SaTokenException("name不能为空");
		}
		if (value != null && value.indexOf(";") > -1) {
			throw new SaTokenException("无效Value：" + value);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(name + "=" + value);
		if(maxAge >= 0) {
			sb.append("; Max-Age=" + maxAge);
			String expires;
			if(maxAge == 0) {
				expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
			} else {
				expires = OffsetDateTime.now().plusSeconds(maxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
			}
			sb.append("; Expires=" + expires);
		}
		if(SaFoxUtil.isEmpty(domain) == false) {
			sb.append("; Domain=" + domain);
		}
		if(SaFoxUtil.isEmpty(path) == false) {
			sb.append("; Path=" + path);
		}
		if(secure) {
			sb.append("; Secure");
		}
		if(httpOnly) {
			sb.append("; HttpOnly");
		}
		if(SaFoxUtil.isEmpty(sameSite) == false) {
			sb.append("; sameSite=" + sameSite);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
		System.out.println("expires = " + expires);
		String expires1 = OffsetDateTime.now().plusSeconds(3600).format(DateTimeFormatter.RFC_1123_DATE_TIME);
		System.out.println("expires1 = " + expires1);
		boolean b = "11:22;55".indexOf(";") > -1;
		System.out.println("b = " + b);

	}
}
