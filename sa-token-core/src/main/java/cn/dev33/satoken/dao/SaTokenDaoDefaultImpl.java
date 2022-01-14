package cn.dev33.satoken.dao;

import cn.dev33.satoken.SaManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SaTokenDaoDefaultImpl implements SaTokenDao {

	@Override
	public String get(String key) {
		return null;
	}

	@Override
	public void set(String key, String value, long timeout) {

	}

	@Override
	public void update(String key, String value) {

	}

	/**
	 * 执行数据清理的线程
	 */
	public Thread refreshThread;

	/**
	 * 是否继续执行数据清理的线程标记
	 */
	public volatile boolean refreshFlag;
	public Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();
	public Map<String, Long> expireMap = new ConcurrentHashMap<String, Long>();

	public SaTokenDaoDefaultImpl() {
		initRefreshThread();
	}

	public void refreshDataMap() {
		Iterator<String> keys = expireMap.keySet().iterator();
		while (keys.hasNext()) {
			clearKeyByTimeout(keys.next());
		}
	}

	void clearKeyByTimeout(String key) {
		Long expirationTime = expireMap.get(key);
		// 清除条件: 如果不为空 && 不是[永不过期] && 已经超过过期时间
		if (expirationTime != null && expirationTime != SaTokenDao.NEVER_EXPIRE && expirationTime < System.currentTimeMillis()) {
			dataMap.remove(key);
			expireMap.remove(key);
		}
	}

	public void initRefreshThread() {
		if (SaManager.getConfig().getDataRefreshPeriod() <= 0) {
			return;
		}
		// 启动定时刷新
		this.refreshFlag = true;
		this.refreshThread = new Thread(() -> {
			for (; ; ) {
				try {
					try {
						if (refreshFlag == false) {
							return;
						}
						// 执行清理
						refreshDataMap();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 休眠N秒
					int dataRefreshPeriod = SaManager.getConfig().getDataRefreshPeriod();
					if (dataRefreshPeriod <= 0) {
						dataRefreshPeriod = 1;
					}
					Thread.sleep(dataRefreshPeriod * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		this.refreshThread.start();
	}

	@Override
	public void delete(String key) {
		dataMap.remove(key);
		expireMap.remove(key);
	}

	@Override
	public long getTimeout(String key) {
		return 0;
	}

	@Override
	public void updateTimeout(String key, long timeout) {

	}

	@Override
	public Object getObject(String key) {
		return null;
	}

	@Override
	public void setObject(String key, Object object, long timeout) {

	}

	@Override
	public void updateObject(String key, Object object) {

	}

	@Override
	public void deleteObject(String key) {
		dataMap.remove(key);
		expireMap.remove(key);
	}

	@Override
	public long getObjectTimeout(String key) {
		return 0;
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {

	}

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size) {
		return null;
	}

	public void endRefreshThread() {

	}
}
