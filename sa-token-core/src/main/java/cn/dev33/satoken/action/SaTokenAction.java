package cn.dev33.satoken.action;

import cn.dev33.satoken.session.SaSession;

public interface SaTokenAction {

    /**
     * 创建一个Token
     */
    public String createToken(Object loginId, String loginType);
    public SaSession createSession(String sessionId);


}

