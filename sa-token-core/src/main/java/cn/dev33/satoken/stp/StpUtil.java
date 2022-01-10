package cn.dev33.satoken.stp;

public class StpUtil {

    public static final String TYPE = "login";
    public static StpLogic stpLogic = new StpLogic(TYPE);
    public static String getLoginType(){
        return stpLogic.getLoginType();
    }
}
