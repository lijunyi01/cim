package com.crossoverjie.cim.wsserver.config;

import com.crossoverjie.cim.wsserver.interceptor.websocket.AuthHandshakeInterceptor;
import com.crossoverjie.cim.wsserver.interceptor.websocket.MyChannelInterceptor;
import com.crossoverjie.cim.wsserver.interceptor.websocket.MyHandshakeHandler;
import com.crossoverjie.cim.wsserver.interceptor.websocket.WebSocketErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket相关配置
 *
 * @author zifangsky
 * @date 2018/9/30
 * @since 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;

    @Autowired
    private MyHandshakeHandler myHandshakeHandler;

    @Autowired
    private WebSocketErrorHandler webSocketErrorHandler;

    @Autowired
    private MyChannelInterceptor myChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 是否要用.withSockJS()取决于客户端，要和客户端保持一致
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/stomp-websocket").setAllowedOrigins("*");
        //registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/stomp-websocket").setAllowedOrigins("*").withSockJS();

        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/chat-websocket")
                .addInterceptors(authHandshakeInterceptor)
//                .setHandshakeHandler(myHandshakeHandler)
                .setAllowedOrigins("*");
//        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/chat-websocket")
//                .addInterceptors(authHandshakeInterceptor)
//                .setHandshakeHandler(myHandshakeHandler)
//                .setAllowedOrigins("*")
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //客户端需要把消息发送到/message/xxx地址
        registry.setApplicationDestinationPrefixes("/message");
        //给指定用户发送消息的路径前缀，默认值是/user/
        registry.setUserDestinationPrefix("/user/");
        //服务端广播/单播消息的路径前缀，客户端需要相应订阅/topic/yyy这个地址的消息
        registry.enableSimpleBroker("/topic","/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(myChannelInterceptor);
    }

}
