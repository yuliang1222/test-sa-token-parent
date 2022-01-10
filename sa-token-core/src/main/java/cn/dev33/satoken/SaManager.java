package cn.dev33.satoken;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.HashMap;
import java.util.Map;

public class SaManager {
    //volatile关键字能保证可见性和有序性，但是不保证原子性。因此并不能保证线程安全。
    private volatile static SaTokenDao saTokenDao;
    public volatile static SaTokenConfig config;
    public static Map<String, StpLogic> stpLogicMap = new HashMap<String, StpLogic>();

    public static SaTokenDao getSaTokenDao() {
        if (saTokenDao == null) {
            synchronized (SaManager.class) {
                if (saTokenDao == null) {
                    setSaTokenDao(new SaTokenDaoDefaultImpl());
                }
            }
        }
        return saTokenDao;
    }

    public static void setSaTokenDao(SaTokenDao saTokenDao) {
        if((SaManager.saTokenDao instanceof SaTokenDaoDefaultImpl)) {
            ((SaTokenDaoDefaultImpl)SaManager.saTokenDao).endRefreshThread();
        }
        SaManager.saTokenDao = saTokenDao;
    }

    public static SaTokenConfig getConfig() {
        if (config == null) {
            synchronized (SaManager.class) {
                if (config == null) {
                    setConfig(SaTokenConfigFactory.createConfig());
                }
            }
        }
        return config;
    }

    public static void setConfig(SaTokenConfig config) {
        SaManager.config = config;
        if(config.getIsPrint()) {
            SaFoxUtil.printSaToken();
        }
        StpUtil.getLoginType();



    }

    public static void putStpLogic(StpLogic stpLogic) {
        stpLogicMap.put(stpLogic.getLoginType(), stpLogic);
    }
}