package cn.dev33.satoken.config;

public class SaCookieConfig {
    /**
     * 域（写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）
     */
    private String domain;
    private String path;
    private Boolean secure = false;
    private Boolean httpOnly = false;
    /**
     * 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
     */
    private String sameSite;

    public String getDomain() {
        return domain;
    }
    public SaCookieConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }
    public String getPath() {
        return path;
    }

    public SaCookieConfig setPath(String path) {
        this.path = path;
        return this;
    }

    public Boolean getSecure() {
        return secure;
    }

    public SaCookieConfig setSecure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public SaCookieConfig setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public String getSameSite() {
        return sameSite;
    }

    public SaCookieConfig setSameSite(String sameSite) {
        this.sameSite = sameSite;
        return this;
    }

    @Override
    public String toString() {
        return "SaCookieConfig [domain=" + domain + ", path=" + path + ", secure=" + secure + ", httpOnly=" + httpOnly
                + ", sameSite=" + sameSite + "]";

    }
}