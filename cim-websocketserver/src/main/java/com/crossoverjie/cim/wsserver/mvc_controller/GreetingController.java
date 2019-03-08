package com.crossoverjie.cim.wsserver.mvc_controller;

import com.crossoverjie.cim.wsserver.model.websocket.Greeting;
import com.crossoverjie.cim.wsserver.model.websocket.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Greeting
 * @author zifangsky
 * @date 2018/9/30
 * @since 1.0.0
 */
@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greeting")
    public HelloMessage greeting(Greeting greeting) {
        return new HelloMessage("Hello," + greeting.getName() + "!");
    }
}
