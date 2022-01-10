package cn.dev33.satoken.action;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

public class SaTokenActionDefaultImpl implements SaTokenAction{
    @Override
    public String createToken(Object loginId, String loginType) {
        String tokenStyle = SaManager.getConfig().getTokenStyle();

        return null;
    }

    @Override
    public SaSession createSession(String sessionId) {
        return null;
    }

    @Override
    public boolean hasElement(List<String> list, String element) {
        return false;
    }

    @Override
    public void checkMethodAnnotation(Method method) {

    }

    @Override
    public void validateAnnotation(AnnotatedElement target) {

    }
}
