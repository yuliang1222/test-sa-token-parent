package cn.dev33.satoken.session;

import cn.dev33.satoken.SaManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class SaSession implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String ROLE_LIST = "ROLE_LIST";
    public static final String PERMISSION_LIST = "PERMISSION_LIST";
    private String id;
    private long createTime;
    private final Map<String, Object> dataMap = new ConcurrentHashMap<>();
    public SaSession() {}
    public SaSession(String id) {
        this.id = id;
        this.createTime = System.currentTimeMillis();
         //TODO 通知监听器
    }
    public String getId() {
        return id;
    }
    public SaSession setId(String id) {
        this.id = id;
        return this;
    }
    public long getCreateTime() {
        return createTime;
    }
    public SaSession setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }
    private final List<TokenSign> tokenSignList = new Vector<>();
    public List<TokenSign> getTokenSignList() {
        return new Vector<>(tokenSignList);
    }
    public TokenSign getTokenSign(String tokenValue) {
        for (TokenSign tokenSign : getTokenSignList()) {
            if (tokenSign.getValue().equals(tokenValue)) {
                return tokenSign;
            }
        }
        return null;
    }
    public void addTokenSign(TokenSign tokenSign) {
        for (TokenSign tokenSign2 : getTokenSignList()) {
            // 如果已经存在于列表中，则无需再次添加
            if (tokenSign2.getValue().equals(tokenSign.getValue())) {
                return;
            }
        }
        // 添加并更新
        tokenSignList.add(tokenSign);
        update();
    }

    private void update() {
        //更新Session（从持久库更新刷新一下）
        SaManager.getSaTokenDao().updateSession(this);
    }


}
