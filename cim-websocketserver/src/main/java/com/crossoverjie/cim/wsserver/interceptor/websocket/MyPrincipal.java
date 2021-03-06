package com.crossoverjie.cim.wsserver.interceptor.websocket;

import java.security.Principal;

/**
 * 自定义{@link Principal}
 *
 * @author zifangsky
 * @date 2018/10/11
 * @since 1.0.0
 */
public class MyPrincipal implements Principal {
    private String loginName;

    public MyPrincipal(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getName() {
        return loginName;
    }
}
