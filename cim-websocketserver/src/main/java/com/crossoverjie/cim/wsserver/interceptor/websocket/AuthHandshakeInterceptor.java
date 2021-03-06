package com.crossoverjie.cim.wsserver.interceptor.websocket;

import com.crossoverjie.cim.wsserver.common.Constants;
import com.crossoverjie.cim.wsserver.model.User;
import com.crossoverjie.cim.wsserver.util.SpringContextUtils;
import io.netty.handler.codec.stomp.StompHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.Map;

/**
 * 自定义{@link org.springframework.web.socket.server.HandshakeInterceptor}，实现“需要登录才允许连接WebSocket”
 *
 * @author zifangsky
 * @date 2018/10/11
 * @since 1.0.0
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
//        HttpSession session = SpringContextUtils.getSession();
//        User loginUser = (User) session.getAttribute(Constants.SESSION_USER);
        // 握手阶段使用http协议，只能通过url带参数方式传token
        // var client = Stomp.client('ws://127.0.0.1:8081/chat-websocket');client.connect({token: 'token'}, function (succ) {...} 通过该方式传的是stomp的header，不特殊处理无法解析出来！
        ServletServerHttpRequest req = (ServletServerHttpRequest) serverHttpRequest;
        //获取token认证
        String token = req.getServletRequest().getParameter("token");


        if(token != null){
            // todo：验证token
            //logger.debug(MessageFormat.format("用户{0}请求建立WebSocket连接", loginUser.getUsername()));
            return true;
        }else{
            logger.error("未登录系统，禁止连接WebSocket");
            return false;
        }

    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

}
