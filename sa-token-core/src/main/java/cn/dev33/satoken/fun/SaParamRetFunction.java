package cn.dev33.satoken.fun;
@FunctionalInterface
public interface SaParamRetFunction<T,R> {
	public R run(T param);
}
