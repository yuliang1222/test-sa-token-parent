package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaCookie;
import cn.dev33.satoken.context.model.SaMode;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static cn.dev33.satoken.SaManager.getSaTokenDao;

/**
 * Sa-Token 权限认证，逻辑实现类
 *
 * @author kong
 */
public class StpLogic {
	public String loginType;

	public StpLogic(String loginType) {
		this.loginType = loginType;
		// 在 SaTokenManager 中记录下此 StpLogic，以便根据 LoginType 进行查找此对象
		SaManager.putStpLogic(this);
	}

	public String getLoginType() {
		return loginType;
	}


	/**
	 * 根据注解(@SaCheckSafe)鉴权
	 *
	 * @param at 注解对象
	 */
	public void checkByAnnotation(SaCheckSafe at) {
		this.checkSafe();
	}

	public void checkSafe() {
		if (isSafe() == false) {
			throw new NotSafeException();
		}
	}

	private boolean isSafe() {

		return false;
	}

	/**
	 * 根据注解(@SaCheckRole)鉴权
	 *
	 * @param at 注解对象
	 */
	public void checkByAnnotation(SaCheckRole at) {
		String[] roleArray = at.value();
		if (at.mode() == SaMode.AND) {
			this.checkRoleAnd(roleArray);
		} else {
			this.checkRoleOr(roleArray);
		}
	}

	private void checkRoleOr(String[] roleArray) {
		Object loginId = getLoginId();
		List<String> roleList = getRoleList(loginId);
		for (String role : roleArray) {
			if (hasElement(roleList, role)) {
				// 有的话提前退出
				return;
			}
		}
		if (roleArray.length > 0) {
			throw new NotRoleException(roleArray[0], this.loginType);
		}
	}

	private void checkRoleAnd(String... roleArray) {
		Object loginId = getLoginId();
		List<String> roleList = getRoleList(loginId);
		for (String role : roleArray) {
			if (!hasElement(roleList, role)) {
				throw new NotRoleException(role, this.loginType);
			}
		}
	}

	public void checkByAnnotation(SaCheckPermission at) {
		String[] permissionArray = at.value();
		try {
			if (at.mode() == SaMode.AND) {
				this.checkPermissionAnd(permissionArray);
			} else {
				this.checkPermissionOr(permissionArray);
			}
		} catch (NotPermissionException e) {
			// 权限认证未通过,再开始角色认证.
			if (at.orRole().length > 0) {
				for (String role : at.orRole()) {
					String[] rArray = SaFoxUtil.convertStringToArray(role);
					if (hasRoleAnds(rArray)) {
						return;
					}
				}
			}
			throw e;
		}

	}

	private boolean hasRoleAnds(String... roleArray) {
		try {
			checkRoleAnd(roleArray);
			return true;
		} catch (NotLoginException | NotRoleException e) {
			return false;
		}
	}

	private void checkPermissionOr(String[] permissionArray) {
		Object loginId = getLoginId();
		List<String> permissionList = getPermissionList(loginId);
		for (String permission : permissionArray) {
			if (hasElement(permissionList, permission)) {
				return;
			}
		}
		if (permissionArray.length > 0) {
			throw new NotPermissionException(permissionArray[0], this.loginType);
		}
	}

	private void checkPermissionAnd(String... permissionArray) {
		Object loginId = getLoginId();
		List<String> permissionList = getPermissionList(loginId);
		for (String permission : permissionList) {
			if (hasElement(permissionList, permission)) {
				throw new NotPermissionException(permission, this.loginType);
			}
		}
	}

	private List<String> getPermissionList(Object loginId) {
		return SaManager.getStpInterface().getPermissionList(loginId, loginType);
	}


	private boolean hasElement(List<String> list, String element) {
		return SaStrategy.me.hasElement.apply(list, element);
	}

	private List<String> getRoleList(Object loginId) {
		return SaManager.getStpInterface().getRoleList(loginId, loginType);
	}

	/**
	 * 根据注解(@SaCheckLogin)鉴权
	 *
	 * @param checkLogin 注解对象
	 */
	public void checkByAnnotation(SaCheckLogin checkLogin) {
		this.checkLogin();
	}

	/**
	 * 检验当前会话是否已经登录，如未登录，则抛出异常
	 */
	private void checkLogin() {
		getLoginId();
	}

	/**
	 * 获取当前会话账号id, 如果未登录，则抛出异常
	 *
	 * @return 账号id
	 */
	private Object getLoginId() {
		// 如果正在[临时身份切换], 则返回临时身份
//		if(isSwitch()) {
//			return getSwitchLoginId();
//		}
		return null;
	}

	public void login(Object id) {
		login(id, new SaLoginModel());
	}

	public SaTokenDao getSaTokenDao() {
		return SaManager.getSaTokenDao();
	}

	public long getDisableTime(Object loginId) {
		return getSaTokenDao().getTimeout(splicingKeyDisable(loginId));
	}

	public String splicingKeyDisable(Object loginId) {
		return getConfig().getTokenName() + ":" + loginType + ":disable:" + loginId;
	}

	public SaTokenConfig getConfig() {
		// 为什么再次代理一层? 为某些极端业务场景下[需要不同StpLogic不同配置]提供便利
		return SaManager.getConfig();
	}

	public boolean getConfigOfIsShare() {
		return getConfig().getIsShare();
	}
	public String splicingKeySession(Object loginId) {
		return getConfig().getTokenName() + ":" + loginType + ":session:" + loginId;
	}
	public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
		SaSession session = getSaTokenDao().getSession(sessionId);
		if(session == null && isCreate) {
			session = SaStrategy.me.createSession.apply(sessionId);
			getSaTokenDao().setSession(session, getConfig().getTimeout());
		}
		return session;
	}
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionBySessionId(splicingKeySession(loginId), isCreate);
	}
	public List<String> getTokenValueListByLoginId(Object loginId, String device) {
		SaSession session = getSessionByLoginId(loginId, false);
		if (session == null) {
			return Collections.emptyList();
		}
		List<TokenSign> tokenSignList = session.getTokenSignList();
		List<String> tokenValueList = new ArrayList<>();
		for (TokenSign tokenSign : tokenSignList) {
			if (device == null || tokenSign.getDevice().equals(device)) {
				tokenValueList.add(tokenSign.getValue());
			}
		}
		return tokenValueList;
	}

	public String getTokenValueByLoginId(Object loginId, String device) {
		List<String> tokenValueList = getTokenValueListByLoginId(loginId, device);
		return tokenValueList.size() == 0 ? null : tokenValueList.get(tokenValueList.size() - 1);
	}
	public String splicingKeyTokenValue(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginType + ":token:" + tokenValue;
	}
	public void updateTokenToIdMapping(String tokenValue, Object loginId) {
		SaTokenException.throwBy(SaFoxUtil.isEmpty(loginId), "LoginId 不能为空");
		getSaTokenDao().update(splicingKeyTokenValue(tokenValue), loginId.toString());
	}
	public void replaced(Object loginId, String device) {
		clearTokenCommonMethod(loginId,device,tokenValue->{
			// 将此token标记为已被顶替
			updateTokenToIdMapping(tokenValue, NotLoginException.BE_REPLACED);
			SaManager.getSaTokenListener().doReplaced(loginType,loginId,tokenValue);
		},false);
	}

	/**
	 * 封装 注销、踢人、顶人 三个动作的相同代码（无API含义方法）
	 * @param loginId 账号id
	 * @param device 设备标识
	 * @param appendFun 追加操作
	 * @param isLogoutSession 是否注销 User-Session
	 */
	protected void clearTokenCommonMethod(Object loginId, String device, Consumer<String> appendFun, boolean isLogoutSession) {
		SaSession session = getSessionByLoginId(loginId, false);
		if (session == null) {
			return;
		}
		// 2.循环token签名列表,开始删除相关信息.
		for (TokenSign tokenSign : session.getTokenSignList()) {
			if (device == null || tokenSign.getDevice().equals(device)) {
				// ------ 共有操作
				// s1. 获取token
				String tokenValue = tokenSign.getValue();
				// s2. 清理掉[token-last-activity]
				clearLastActivity(tokenValue);
				// s3. 从token签名列表移除
				session.removeTokenSign(tokenValue);
				// -------- 追加操作
				appendFun.accept(tokenValue);
			}
		}
		// 3. 尝试注销session
		if (isLogoutSession) {
			session.logoutByTokenSignCountToZero();
		}
	}

	private void clearLastActivity(String tokenValue) {
		// 如果token == null 或者 设置了[永不过期], 则立即返回
		if (tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
			return;
		}
		// 删除[最后操作时间]
		getSaTokenDao().delete(splicingKeyLastActivityTime(tokenValue));
		// 清除标记
		SaHolder.getStorage().delete(SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY);
	}
	public String splicingKeyLastActivityTime(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginType + ":last-activity:" + tokenValue;
	}
	public void login(Object id, SaLoginModel loginModel) {
		SaTokenException.throwByNull(id, "账号id不能为空");
		// ------ 0、前置检查：如果此账号已被封禁.
		SaTokenException.throwByNull(id, "账号id不能为空");
		if (isDisable(id)) {
			throw new DisableLoginException(loginType, id, getDisableTime(id));
		}
		// ------ 1、初始化 loginModel
		SaTokenConfig config = getConfig();
		loginModel.build(config);
		// ------ 2、生成一个token
		String tokenValue = null;
		// --- 如果允许并发登录
		if (config.getIsConcurrent()) {
			// 如果配置为共享token, 则尝试从Session签名记录里取出token
			if (getConfigOfIsShare()) {
				tokenValue = getTokenValueByLoginId(id, loginModel.getDeviceOrDefault());
			}
		}else {
		// --- 如果不允许并发登录，则将这个账号的历史登录标记为：被顶下线
			replaced(id,loginModel.getDevice());
		}
		// 如果至此，仍未成功创建tokenValue, 则开始生成一个
		if (tokenValue == null) {
			tokenValue = createTokenValue(id, loginModel.getDeviceOrDefault(), loginModel.getTimeout());
		}
		// ------ 3. 获取 User-Session , 续期
		SaSession session = getSessionByLoginId(id, true);
		session.updateMinTimeout(loginModel.getTimeout());
		// 在 User-Session 上记录token签名
		session.addTokenSign(tokenValue,loginModel.getDeviceOrDefault());
		// ------ 4. 持久化其它数据
		// token -> id 映射关系
		saveTokenToIdMapping(tokenValue, id, loginModel.getTimeout());
		// 在当前会话写入tokenValue
		setTokenValue(tokenValue, loginModel.getCookieTimeout());
		// 写入 [token-last-activity]
		setLastActivityToNow(tokenValue);
		// $$ 通知监听器，账号xxx 登录成功
		SaManager.getSaTokenListener().doLogin(loginType,id,loginModel);
	}
	/**
	 * 写入指定token的 [最后操作时间] 为当前时间戳
	 * @param tokenValue 指定token
	 */
	private void setLastActivityToNow(String tokenValue) {
		// 如果token == null 或者 设置了[永不过期], 则立即返回
		if (tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
			return;
		}
		// 将[最后操作时间]标记为当前时间戳
		getSaTokenDao().set(splicingKeyLastActivityTime(tokenValue),String.valueOf(System.currentTimeMillis()),getConfig().getTimeout());

	}
	public void setTokenValue(String tokenValue){
		setTokenValue(tokenValue, (int)SaManager.getConfig().getTimeout());
	}
	public void setTokenValue(String tokenValue, int cookieTimeout){
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return;
		}
		// 1. 将token保存到[存储器]里
		setTokenValueToStorage(tokenValue);

		// 2. 将 Token 保存到 [Cookie] 里
		if (getConfig().getIsReadCookie()) {
			setTokenValueToCookie(tokenValue, cookieTimeout);
		}
	}
	public void setTokenValueToCookie(String tokenValue, int cookieTimeout){
		SaCookieConfig cfg = getConfig().getCookie();
		SaCookie cookie = new SaCookie()
				.setName(getTokenName())
				.setValue(tokenValue)
				.setMaxAge(cookieTimeout)
				.setDomain(cfg.getDomain())
				.setPath(cfg.getPath())
				.setSecure(cfg.getSecure())
				.setHttpOnly(cfg.getHttpOnly())
				.setSameSite(cfg.getSameSite())
				;
		SaHolder.getResponse().addCookie(cookie);
	}
	public String getTokenName() {
		return splicingKeyTokenName();
	}
	public String splicingKeyTokenName() {
		return getConfig().getTokenName();
	}
	public void setTokenValueToStorage(String tokenValue){
		// 1. 将token保存到[存储器]里
		SaStorage storage = SaHolder.getStorage();

		// 2. 如果打开了 Token 前缀模式，则拼接上前缀
		String tokenPrefix = getConfig().getTokenPrefix();
		if(SaFoxUtil.isEmpty(tokenPrefix) == false) {
			storage.set(splicingKeyJustCreatedSave(), tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT + tokenValue);
		} else {
			storage.set(splicingKeyJustCreatedSave(), tokenValue);
		}

		// 3. 写入 (无前缀)
		storage.set(SaTokenConsts.JUST_CREATED_NOT_PREFIX, tokenValue);
	}
	public String splicingKeyJustCreatedSave() {
//		return SaTokenConsts.JUST_CREATED_SAVE_KEY + loginType;
		return SaTokenConsts.JUST_CREATED;
	}
	public void saveTokenToIdMapping(String tokenValue, Object loginId, long timeout) {
		getSaTokenDao().set(splicingKeyTokenValue(tokenValue), String.valueOf(loginId), timeout);
	}

	public String createTokenValue(Object loginId, String device, long timeout) {
		return SaStrategy.me.createToken.apply(loginId, loginType);
	}
	public boolean isDisable(Object loginId) {
		return getSaTokenDao().get(splicingKeyDisable(loginId)) != null;

	}

	public String getTokenValue() {
		String tokenValue = getTokenValueNotCut();
		String tokenPrefix = getConfig().getTokenPrefix();
		if(SaFoxUtil.isEmpty(tokenPrefix) == false) {
			if(SaFoxUtil.isEmpty(tokenValue) || tokenValue.startsWith(tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT) == false) {
				tokenValue = null;
			} else {
				// 则裁剪掉前缀
				tokenValue = tokenValue.substring(tokenPrefix.length() + SaTokenConsts.TOKEN_CONNECTOR_CHAT.length());
			}
		}
		// 3. 返回
		return tokenValue;
	}
	/**
	 * 获取当前TokenValue (不裁剪前缀)
	 * @return /
	 */
	public String getTokenValueNotCut(){
		// 0. 获取相应对象
		SaStorage storage = SaHolder.getStorage();
		SaRequest request = SaHolder.getRequest();
		SaTokenConfig config = getConfig();
		String keyTokenName = getTokenName();
		String tokenValue = null;

		// 1. 尝试从Storage里读取
		if(storage.get(splicingKeyJustCreatedSave()) != null) {
			tokenValue = String.valueOf(storage.get(splicingKeyJustCreatedSave()));
		}
		// 2. 尝试从请求体里面读取
		if(tokenValue == null && config.getIsReadBody()){
			tokenValue = request.getParam(keyTokenName);
		}
		// 3. 尝试从header里读取
		if(tokenValue == null && config.getIsReadHead()){
			tokenValue = request.getHeader(keyTokenName);
		}
		// 4. 尝试从cookie里读取
		if(tokenValue == null && config.getIsReadCookie()){
			tokenValue = request.getCookieValue(keyTokenName);
		}

		// 5. 返回
		return tokenValue;
	}

	public SaTokenInfo getTokenInfo() {
		SaTokenInfo info = new SaTokenInfo();
		info.tokenName = getTokenName();
		info.tokenValue = getTokenValue();
		info.isLogin = isLogin();
		info.loginId = getLoginIdDefaultNull();
		info.loginType = getLoginType();
		info.tokenTimeout = getTokenTimeout();
		info.sessionTimeout = getSessionTimeout();
		info.tokenSessionTimeout = getTokenSessionTimeout();
		info.tokenActivityTimeout = getTokenActivityTimeout();
		info.loginDevice = getLoginDevice();
		return info;
	}

	private Boolean isLogin() {
		// 判断条件：不为null，并且不在异常项集合里
		return getLoginIdDefaultNull() != null;
	}

	private Boolean getLoginIdDefaultNull() {
		// 如果正在[临时身份切换]
		//
		return null;
	}
//	private boolean isSwitch() {
//		return SaHolder.getStorage().get(splicingKeySwitch()) != null;
//	}
}
