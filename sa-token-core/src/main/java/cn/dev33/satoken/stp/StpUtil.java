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
}
