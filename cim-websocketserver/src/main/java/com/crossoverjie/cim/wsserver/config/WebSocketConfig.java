package com.crossoverjie.cim.wsserver.config;

import com.crossoverjie.cim.wsserver.interceptor.websocket.AuthHandshakeInterceptor;
import com.crossoverjie.cim.wsserver.interceptor.websocket.MyChannelInterceptor;
import com.crossoverjie.cim.wsserver.interceptor.websocket.MyHandshakeHandler;
import com.crossoverjie.cim.wsserver.interceptor.websocket.WebSocketErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

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

        // 自定义调度器，用于控制心跳线程
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 线程池线程数，心跳连接断开线程
        taskScheduler.setPoolSize(1);
        // 线程名前缀
        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        // 初始化
        taskScheduler.initialize();

        // 客户端需要把消息发送到/message/xxx地址； 表示所有以/message 开头的客户端消息或请求
        // 都会路由到带有@MessageMapping 注解的方法中
        registry.setApplicationDestinationPrefixes("/message");
        // 给指定用户发送消息的路径前缀，默认值是/user/
        registry.setUserDestinationPrefix("/user/");
        //服务端广播/单播消息的路径前缀，客户端需要相应订阅/topic/yyy这个地址的消息
        registry.enableSimpleBroker("/topic","/user")
               .setHeartbeatValue(new long[]{10000,10000})  // 设置心跳，第一值表示server最小能保证发的心跳间隔毫秒数, 第二个值代码server希望client发的心跳间隔毫秒数
               .setTaskScheduler(taskScheduler);
    }

    /**
     * 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        /*
         * 1. setMessageSizeLimit 设置消息缓存的字节数大小 字节
         * 2. setSendBufferSizeLimit 设置websocket会话时，缓存的大小 字节
         * 3. setSendTimeLimit 设置消息发送会话超时时间，毫秒
         */
        registration.setMessageSizeLimit(10240)
                .setSendBufferSizeLimit(10240)
                .setSendTimeLimit(10000);

//        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
//            @Override
//            public WebSocketHandler decorate(final WebSocketHandler handler) {
//                return new WebSocketHandlerDecorator(handler) {
//                    @Override
//                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
//                        // 客户端与服务器端建立连接后，此处记录谁上线了
//                        //String username = session.getPrincipal().getName();
//                        //log.info("online: " + username);
//                        super.afterConnectionEstablished(session);
//                    }
//
//                    @Override
//                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//                        // 客户端与服务器端断开连接后，此处记录谁下线了
//                        //String username = session.getPrincipal().getName();
//                        //log.info("offline: " + username);
//                        super.afterConnectionClosed(session, closeStatus);
//                    }
//                };
//            }
//        });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(myChannelInterceptor);
    }

}
