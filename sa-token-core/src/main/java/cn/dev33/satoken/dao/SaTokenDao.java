package cn.dev33.satoken.dao;

import cn.dev33.satoken.session.SaSession;

import java.util.List;

public interface SaTokenDao {
    public static final long NEVER_EXPIRE = -1;
    public static final long NOT_VALUE_EXPIRE = -2;
    public String get(String key);
    public void set(String key, String value, long timeout);
    public void update(String key, String value);
    public void delete(String key);
    public long getTimeout(String key);
    public void updateTimeout(String key, long timeout);
    public Object getObject(String key);
    public void setObject(String key, Object object, long timeout);
    public void updateObject(String key, Object object);
    public void deleteObject(String key);
    public long getObjectTimeout(String key);
    public void updateObjectTimeout(String key, long timeout);
    public default SaSession getSession(String sessionId) {
        return (SaSession)getObject(sessionId);
    }
    public default void setSession(SaSession session, long timeout) {
        setObject(session.getId(), session, timeout);
    }
    public default void updateSession(SaSession session) {
        updateObject(session.getId(), session);
    }
    public default void deleteSession(String sessionId) {
        deleteObject(sessionId);
    }
    public default long getSessionTimeout(String sessionId) {
        return getObjectTimeout(sessionId);
    }
    public default void updateSessionTimeout(String sessionId, long timeout) {
        updateObjectTimeout(sessionId, timeout);
    }
    public List<String> searchData(String prefix, String keyword, int start, int size);

}
