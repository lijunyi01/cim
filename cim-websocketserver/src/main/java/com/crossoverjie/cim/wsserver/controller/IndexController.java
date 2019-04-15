package com.crossoverjie.cim.wsserver.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.wsserver.service.UserService;
import com.crossoverjie.cim.wsserver.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.wsserver.vo.res.SendMsgResVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import org.springframework.boot.actuate.metrics.CounterService;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserService userService;


    /**
     * 统计 service
     */
//    @Autowired
//    private CounterService counterService;

    /**
     * 向服务端发任务，让服务端向客户端推消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("服务端发送消息")
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO){
        BaseResponse<SendMsgResVO> res = new BaseResponse();

        //通过websocket向客户端广播消息
        template.convertAndSend("/topic/greeting",sendMsgReqVO);

        //counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }

    /**
     * 向服务端发任务，向指定客户端推送消息

     * @return
     */
    @RequestMapping(value = "sendUserMsg",method = RequestMethod.POST)
    @ResponseBody
    public String sendUserMsg(@RequestBody SendMsgReqVO msg){


        String destUserId = msg.getUserId() + "";


        if (userService.isUserOnLine(destUserId)){
            // 对应的客户端代码：client.subscribe("/user/" + userId + "/msg", onMessage);
            template.convertAndSendToUser(destUserId + "","/msg",msg);
            return "发送成功";
        }else {

            //todo: 将消息存入redis
//            Long aLong = onlineUserService.addUserMassage(destUserId, msg);
            return "此用户不在线，消息已入缓存";
        }
    }

}
