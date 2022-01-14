package cn.dev33.satoken.action;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class SaTokenActionDefaultImpl implements SaTokenAction{
    @Override
    public String createToken(Object loginId, String loginType) {
        // 根据配置的tokenStyle生成不同风格的token
        String tokenStyle = SaManager.getConfig().getTokenStyle();
        if(SaTokenConsts.TOKEN_STYLE_UUID.equals(tokenStyle)) {
            return UUID.randomUUID().toString();
        }
        if(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID.equals(tokenStyle)) {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        // 32位随机字符串
        if(SaTokenConsts.TOKEN_STYLE_RANDOM_32.equals(tokenStyle)) {
            return SaFoxUtil.getRandomString(32);
        }
        if(SaTokenConsts.TOKEN_STYLE_TIK.equals(tokenStyle)) {
            return SaFoxUtil.getRandomString(2) + "_" + SaFoxUtil.getRandomString(14) + "_" + SaFoxUtil.getRandomString(16) + "__";
        }
        return UUID.randomUUID().toString();
    }

    @Override
    public SaSession createSession(String sessionId) {
        return new SaSession(sessionId);
    }
    /**
     * 判断：集合中是否包含指定元素（模糊匹配）
     */
    @Override
    public boolean hasElement(List<String> list, String element) {
        if(list == null || list.size() == 0) {
            return false;
        }
        if (list.contains(element)) {
            return true;
        }
        for (String patt : list) {
            if(SaFoxUtil.vagueMatch(patt, element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkMethodAnnotation(Method method) {
        // 先校验 Method 所属 Class 上的注解
        validateAnnotation(method.getDeclaringClass());

        // 再校验 Method 上的注解
        validateAnnotation(method);
    }

    @Override
    public void validateAnnotation(AnnotatedElement target) {
        // 校验 @SaCheckLogin 注解
        SaCheckLogin checkLogin = (SaCheckLogin) SaStrategy.me.getAnnotation.apply(target, SaCheckLogin.class);
        if(checkLogin != null) {
            SaManager.getStpLogic(checkLogin.type()).checkByAnnotation(checkLogin);
        }
        SaCheckRole checkRole = (SaCheckRole) SaStrategy.me.getAnnotation.apply(target, SaCheckRole.class);
        if(checkRole != null) {
            SaManager.getStpLogic(checkRole.type()).checkByAnnotation(checkRole);
        }
        // 校验 @SaCheckPermission 注解
        SaCheckPermission checkPermission = (SaCheckPermission) SaStrategy.me.getAnnotation.apply(target, SaCheckPermission.class);
        if(checkPermission != null) {
            SaManager.getStpLogic(checkPermission.type()).checkByAnnotation(checkPermission);
        }

        SaCheckSafe checkSafe = (SaCheckSafe)SaStrategy.me.getAnnotation.apply(target, SaCheckSafe.class);
        if (checkSafe != null) {
            SaManager.getStpLogic(checkSafe.type()).checkByAnnotation(checkSafe);
        }

        // 校验 @SaCheckBasic 注解
        SaCheckBasic checkBasic = (SaCheckBasic) SaStrategy.me.getAnnotation.apply(target, SaCheckBasic.class);
        if(checkBasic != null) {
            SaBasicUtil.check(checkBasic.realm(), checkBasic.account());
        }
    }
}
