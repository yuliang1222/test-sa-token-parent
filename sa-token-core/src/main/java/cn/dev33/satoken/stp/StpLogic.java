package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaManager;

public class StpLogic {
    public String loginType;
    public StpLogic(String loginType) {
        this.loginType = loginType;
        // 在 SaTokenManager 中记录下此 StpLogic，以便根据 LoginType 进行查找此对象
        SaManager.putStpLogic(this);
    }
    public String getLoginType(){
        return loginType;
    }
}
