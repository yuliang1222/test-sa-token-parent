package cn.dev33.satoken.session;

import java.io.Serializable;

public class TokenSign implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1406115065849845073L;
    private String value;
    private String device;
    public TokenSign() {}
    public TokenSign(String value, String device) {
        this.value = value;
        this.device = device;
    }
    public String getValue() {
        return value;
    }
    public String getDevice() {
        return device;
    }
    @Override
    public String toString() {
        return "TokenSign [value=" + value + ", device=" + device + "]";
    }

}
