package cn.dev33.satoken.stp;

public class StpUtil {

    public static final String TYPE = "login";
    public static StpLogic stpLogic = new StpLogic(TYPE);
    public static String getLoginType(){
        return stpLogic.getLoginType();
    }

    /**
     * 会话登录
     * @param id 账号id，建议的类型：（long | int | String）
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }

    public static void setTokenValue(String tokenValue){
        stpLogic.setTokenValue(tokenValue);
    }

    public static void setTokenValue(String tokenValue, int cookieTimeout){
        stpLogic.setTokenValue(tokenValue, cookieTimeout);
    }

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }
    public static String getTokenValueNotCut(){
        return stpLogic.getTokenValueNotCut();
    }
    public static SaTokenInfo getTokenInfo() {
        return stpLogic.getTokenInfo();
    }

}
