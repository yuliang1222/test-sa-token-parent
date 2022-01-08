package cn.dev33.satoken;

import cn.dev33.satoken.dao.SaTokenDao;

public class SaManager {
    //众所周知，volatile关键字可以让线程的修改立刻通知其他的线程，从而达到数据一致的作用。
    private volatile static SaTokenDao saTokenDao;

    public static SaTokenDao getSaTokenDao() {

    }
}
