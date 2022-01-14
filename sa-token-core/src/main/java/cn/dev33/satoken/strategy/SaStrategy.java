package cn.dev33.satoken.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SaStrategy {
	private SaStrategy() {
	}

	public static final SaStrategy me = new SaStrategy();
	public BiFunction<Object, String, String> createToken = (loginId, loginType) -> {
		return SaManager.getSaTokenAction().createToken(loginId, loginType);
	};
	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 * <p> 参数 [集合, 元素]
	 */
	public BiFunction<List<String>, String, Boolean> hasElement = (list, element) -> {
		return SaManager.getSaTokenAction().hasElement(list, element);
	};
	/**
	 * 从元素上获取注解（注解鉴权内部实现）
	 * <p> 参数 [element元素，要获取的注解类型]
	 */
	public BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation = (element, annotationClass)->{
		// 默认使用jdk的注解处理器
		return element.getAnnotation(annotationClass);
	};

	public SaStrategy setCreateToken(BiFunction<Object, String, String> createToken) {
		this.createToken = createToken;
		return this;
	}
	public SaStrategy setGetAnnotation(BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation) {
		this.getAnnotation = getAnnotation;
		return this;
	}
	public Function<String, SaSession> createSession = (sessionId) -> {
		return SaManager.getSaTokenAction().createSession(sessionId);
	};
}
