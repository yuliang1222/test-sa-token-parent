package cn.dev33.satoken.action;

import cn.dev33.satoken.session.SaSession;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

public interface SaTokenAction {

    /**
     * 创建一个Token
     */
    public String createToken(Object loginId, String loginType);
    public SaSession createSession(String sessionId);
    public boolean hasElement(List<String> list, String element);
    public void checkMethodAnnotation(Method method);
    public void validateAnnotation(AnnotatedElement target);


}

