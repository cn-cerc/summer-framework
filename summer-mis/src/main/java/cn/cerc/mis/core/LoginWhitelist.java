package cn.cerc.mis.core;

import cn.cerc.mis.config.AppStaticFileDefault;
import cn.cerc.mis.rds.StubHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户免登录验证白名单
 */
public enum LoginWhitelist {

    INSTANCE;

    public static LoginWhitelist getInstance() {
        return INSTANCE;
    }

    private static final List<String> users = new ArrayList<>();

    static {
        // iPhone 上架时专用测试帐号以及专业版体验账号
        users.add("15202406");

        // 基础版体验账号
        users.add("16307405");

        // 喜曼多专用APP测试账号与iPhone上架测试账号
        users.add("47583201");

        // 狼王专用APP测试账号与iPhone上架测试账号
        users.add("15531101");

        // 电商专用测试
        users.add("91100124");

        // 队列专用用户代码
        users.add(StubHandle.DefaultUser);
    }

    public boolean contains(String userCode) {
        return users.contains(userCode);
    }

}
