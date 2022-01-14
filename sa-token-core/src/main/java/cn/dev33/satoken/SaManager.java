package cn.dev33.satoken;

import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerDefaultImpl;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
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

    /**
     * 权限认证 Bean
     */
    private volatile static StpInterface stpInterface;
    public static void setStpInterface(StpInterface stpInterface) {
        SaManager.stpInterface = stpInterface;
    }
    public static StpInterface getStpInterface() {
        if (stpInterface == null) {
            synchronized (SaManager.class) {
                if (stpInterface == null) {
                    setStpInterface(new StpInterfaceDefaultImpl());
                }
            }
        }
        return stpInterface;
    }


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
    private volatile static SaTokenListener saTokenListener;
    public static SaTokenListener getSaTokenListener() {
        if (saTokenListener == null) {
            synchronized (SaManager.class) {
                if (saTokenListener == null) {
                    setSaTokenListener(new SaTokenListenerDefaultImpl());
                }
            }
        }
        return saTokenListener;
    }

    public static void setSaTokenListener(SaTokenListener saTokenListener) {
        SaManager.saTokenListener = saTokenListener;
    }



    private volatile static SaTokenAction saTokenAction;
    public static void setSaTokenAction(SaTokenAction saTokenAction) {
        SaManager.saTokenAction = saTokenAction;
    }
    public static SaTokenAction getSaTokenAction() {
        if (saTokenAction == null) {
            synchronized (SaManager.class) {
                if (saTokenAction == null) {
                    setSaTokenAction(new SaTokenActionDefaultImpl());
                }
            }
        }
        return saTokenAction;
    }

    /**
     * 根据 LoginType 获取对应的StpLogic，如果不存在则抛出异常
     * @param loginType 对应的账号类型
     * @return 对应的StpLogic
     */
    public static StpLogic getStpLogic(String loginType) {
        // 如果type为空则返回框架内置的
        if(loginType == null || loginType.isEmpty()) {
            return StpUtil.stpLogic;
        }

        // 从SaManager中获取
        StpLogic stpLogic = stpLogicMap.get(loginType);
        if(stpLogic == null) {
            /*
             * 此时有两种情况会造成 StpLogic == null
             * 1. loginType拼写错误，请改正 （建议使用常量）
             * 2. 自定义StpUtil尚未初始化（静态类中的属性至少一次调用后才会初始化），解决方法两种
             * 		(1) 从main方法里调用一次
             * 		(2) 在自定义StpUtil类加上类似 @Component 的注解让容器启动时扫描到自动初始化
             */
            throw new SaTokenException("未能获取对应StpLogic，type="+ loginType);
        }

        // 返回
        return stpLogic;
    }

    private volatile static SaTokenContext saTokenContext;

    public static SaTokenContext getSaTokenContextOrSecond() {
        return null;
    }
}