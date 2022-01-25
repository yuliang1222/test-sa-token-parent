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
    public static void login(Object id, String device) {
        stpLogic.login(id, device);
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
    public static void login(Object id, boolean isLastingCookie) {
        stpLogic.login(id, isLastingCookie);
    }
    public static void logoutByTokenValue(String tokenValue) {
        stpLogic.logoutByTokenValue(tokenValue);
    }
    public static void kickout(Object loginId) {
        stpLogic.kickout(loginId);
    }
    public static void kickout(Object loginId, String device) {
        stpLogic.kickout(loginId, device);
    }
    public static void kickoutByTokenValue(String tokenValue) {
        stpLogic.kickoutByTokenValue(tokenValue);
    }
}
